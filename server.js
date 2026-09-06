require("dotenv").config();
const express = require("express");
const path = require("path");
const { createClient } = require("@supabase/supabase-js");

const app = express();
const PORT = process.env.PORT || 3000;

// ==================================================
// Supabase
// ==================================================

const SUPABASE_URL = process.env.SUPABASE_URL;
const SUPABASE_KEY = process.env.SUPABASE_KEY;

let supabase = null;

if (SUPABASE_URL && SUPABASE_KEY) {
    supabase = createClient(
        SUPABASE_URL,
        SUPABASE_KEY
    );

    console.log("Supabase: connected");
} else {
    console.log("Supabase: environment variables missing");
}

// ==================================================
// Environment diagnostics (no secrets exposed)
// ==================================================

app.get("/api/env-status", (req, res) => {
    res.json({
        supabase_url: Boolean(process.env.SUPABASE_URL),
        supabase_key: Boolean(process.env.SUPABASE_KEY),
        supabase_client: Boolean(supabase),
        node_env: process.env.NODE_ENV || "not_set"
    });
});

// ==================================================

// ==================================================
// Middleware
// ==================================================

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use(
    express.static(
        path.join(__dirname, "public")
    )
);

// ==================================================
// Admin authentication
// ==================================================

function requireAdmin(req, res, next) {

    const key = req.headers["x-admin-key"];

    if (!process.env.FORICHI_ADMIN_KEY) {
        return res.status(500).json({
            success: false,
            message: "Admin key is not configured"
        });
    }

    if (!key || key !== process.env.FORICHI_ADMIN_KEY) {
        return res.status(401).json({
            success: false,
            message: "Unauthorized"
        });
    }

    next();
}

// ==================================================
// Web
// ==================================================

app.get("/", (req, res) => {

    res.sendFile(
        path.join(
            __dirname,
            "public",
            "index.html"
        )
    );

});

// ==================================================
// ForiChi API
// ==================================================

app.get("/api", (req, res) => {

    res.json({
        success: true,
        app: "ForiChi",
        version: "1.0",
        message: "ForiChi API is running"
    });

});

// ==================================================
// Health
// ==================================================

app.get("/api/health", async (req, res) => {

    let database = "not_connected";

    if (supabase) {
        try {

            const { error } = await supabase
                .from("users")
                .select("id")
                .limit(1);

            database = error
                ? "error"
                : "connected";

        } catch (error) {

            database = "error";

        }
    }

    res.json({

        success: true,

        app: "ForiChi",

        api: "running",

        database

    });

});

// ==================================================
// Services API
// ==================================================

app.get("/api/services", async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        const { data, error } = await supabase
            .from("services")
            .select("*")
            .order("id", { ascending: false });

        if (error) {
            console.error("Services error:", error);

            return res.status(500).json({
                success: false,
                message: "Database error",
                detail: error.message,
                code: error.code
            });
        }

        res.json({
            success: true,
            count: data.length,
            services: data
        });

    } catch (error) {

        console.error("Services API error:", error);

        res.status(500).json({
            success: false,
            message: "Server error"
        });
    }
});

// ==================================================

// ==================================================

// ==================================================
// Businesses API
// ==================================================

app.get("/api/businesses", async (req, res) => {
    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {
        let query = supabase
            .from("businesses")
            .select("*")
            .eq("is_active", true)
            .order("id", { ascending: false });

        if (req.query.city) {
            query = query.eq("city", req.query.city);
        }

        if (req.query.category) {
            query = query.eq("category", req.query.category);
        }

        const { data, error } = await query;

        if (error) {
            console.error("Businesses error:", error);

            return res.status(500).json({
                success: false,
                message: "Database error",
                detail: error.message,
                code: error.code
            });
        }

        res.json({
            success: true,
            count: data.length,
            businesses: data
        });

    } catch (error) {
        console.error("Businesses API error:", error);

        res.status(500).json({
            success: false,
            message: "Server error"
        });
    }
});

// Admin status
// ==================================================

app.get("/api/admin/status", (req, res) => {

    res.json({

        success: true,

        admin_api: true,

        database:
            supabase
                ? "configured"
                : "not_configured",

        authentication: "pending"

    });

});

// ==================================================
// Admin stats
// ==================================================

app.get("/api/admin/stats", async (req, res) => {

    if (!supabase) {

        return res.json({

            success: false,

            message: "Supabase is not configured"

        });

    }

    try {

        const tables = [
            "users",
            "specialists",
            "services",
            "orders"
        ];

        const stats = {};

        for (const table of tables) {

            const { count, error } =
                await supabase
                    .from(table)
                    .select("*", {
                        count: "exact",
                        head: true
                    });

            stats[table] =
                error ? 0 : (count || 0);
        }

        res.json({

            success: true,

            stats: {

                users: stats.users,
                specialists: stats.specialists,
                services: stats.services,
                orders: stats.orders

            }

        });

    } catch (error) {

        res.status(500).json({

            success: false,

            message: "Database error"

        });

    }

});

// ==================================================
// Admin - Businesses CRUD
// ==================================================

app.post("/api/admin/businesses", requireAdmin, async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        const {
            name,
            category,
            description,
            phone,
            address,
            city,
            image_url,
            latitude,
            longitude,
            is_active
        } = req.body;

        if (!name || !name.trim()) {
            return res.status(400).json({
                success: false,
                message: "Business name is required"
            });
        }

        const { data, error } = await supabase
            .from("businesses")
            .insert([{
                name: name.trim(),
                category: category || null,
                description: description || null,
                phone: phone || null,
                address: address || null,
                city: city || null,
                image_url: image_url || null,
                latitude: latitude ?? null,
                longitude: longitude ?? null,
                is_active: is_active !== false
            }])
            .select()
            .single();

        if (error) {
            return res.status(500).json({
                success: false,
                message: "Database error",
                detail: error.message
            });
        }

        res.status(201).json({
            success: true,
            business: data
        });

    } catch (error) {

        res.status(500).json({
            success: false,
            message: "Server error"
        });

    }

});


app.put("/api/admin/businesses/:id", requireAdmin, async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        const { id } = req.params;

        const allowed = [
            "name",
            "category",
            "description",
            "phone",
            "address",
            "city",
            "image_url",
            "latitude",
            "longitude",
            "is_active"
        ];

        const updates = {};

        for (const field of allowed) {
            if (req.body[field] !== undefined) {
                updates[field] = req.body[field];
            }
        }

        if (updates.name !== undefined) {
            if (!String(updates.name).trim()) {
                return res.status(400).json({
                    success: false,
                    message: "Business name cannot be empty"
                });
            }

            updates.name = String(updates.name).trim();
        }

        if (!Object.keys(updates).length) {
            return res.status(400).json({
                success: false,
                message: "No fields to update"
            });
        }

        const { data, error } = await supabase
            .from("businesses")
            .update(updates)
            .eq("id", id)
            .select()
            .single();

        if (error) {
            return res.status(500).json({
                success: false,
                message: "Database error",
                detail: error.message
            });
        }

        res.json({
            success: true,
            business: data
        });

    } catch (error) {

        res.status(500).json({
            success: false,
            message: "Server error"
        });

    }

});


app.delete("/api/admin/businesses/:id", requireAdmin, async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        const { id } = req.params;

        const { error } = await supabase
            .from("businesses")
            .delete()
            .eq("id", id);

        if (error) {
            return res.status(500).json({
                success: false,
                message: "Database error",
                detail: error.message
            });
        }

        res.json({
            success: true,
            message: "Business deleted"
        });

    } catch (error) {

        res.status(500).json({
            success: false,
            message: "Server error"
        });

    }

});


app.patch("/api/admin/businesses/:id/status", requireAdmin, async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        const { id } = req.params;

        const { is_active } = req.body;

        if (typeof is_active !== "boolean") {
            return res.status(400).json({
                success: false,
                message: "is_active must be boolean"
            });
        }

        const { data, error } = await supabase
            .from("businesses")
            .update({ is_active })
            .eq("id", id)
            .select()
            .single();

        if (error) {
            return res.status(500).json({
                success: false,
                message: "Database error",
                detail: error.message
            });
        }

        res.json({
            success: true,
            business: data
        });

    } catch (error) {

        res.status(500).json({
            success: false,
            message: "Server error"
        });

    }

});


app.get("/api/admin/businesses", requireAdmin, async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        let query = supabase
            .from("businesses")
            .select("*")
            .order("id", { ascending: false });

        if (req.query.city) {
            query = query.eq("city", req.query.city);
        }

        if (req.query.category) {
            query = query.eq("category", req.query.category);
        }

        if (req.query.search) {
            query = query.ilike("name", `%${req.query.search}%`);
        }

        const { data, error } = await query;

        if (error) {
            return res.status(500).json({
                success: false,
                message: "Database error",
                detail: error.message
            });
        }

        res.json({
            success: true,
            count: data.length,
            businesses: data
        });

    } catch (error) {

        res.status(500).json({
            success: false,
            message: "Server error"
        });

    }

});

// ==================================================
// Start
// ==================================================

app.listen(
    PORT,
    "0.0.0.0",
    () => {

        console.log(
            `ForiChi API running on port ${PORT}`
        );

    }
);
