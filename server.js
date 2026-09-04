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
