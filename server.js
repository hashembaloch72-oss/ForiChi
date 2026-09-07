require("dotenv").config();
const express = require("express");
const path = require("path");
const { createClient } = require("@supabase/supabase-js");
const crypto = require("crypto");
const { createUserToken, verifyUserToken, requireUser } = require("./user-auth");

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
        node_env: process.env.NODE_ENV || "not_set",
        user_key: Boolean(process.env.FORICHI_USER_KEY),
        specialist_key: Boolean(process.env.FORICHI_SPECIALIST_KEY)
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

    const auth = req.headers["authorization"];

    if (!process.env.FORICHI_ADMIN_KEY) {
        return res.status(500).json({
            success: false,
            message: "Admin key is not configured"
        });
    }

    if (!auth || !auth.startsWith("Bearer ")) {
        return res.status(401).json({
            success: false,
            message: "Admin token required"
        });
    }

    const token = auth.substring(7);

    try {

        const decoded = Buffer.from(token, "base64url").toString("utf8");
        const [timestamp, signature] = decoded.split(".");

        if (!timestamp || !signature) {
            throw new Error("Invalid token");
        }

        const age = Date.now() - Number(timestamp);

        if (!Number.isFinite(age) || age < 0 || age > 24 * 60 * 60 * 1000) {
            throw new Error("Token expired");
        }

        const expected = crypto
            .createHmac("sha256", process.env.FORICHI_ADMIN_KEY)
            .update(timestamp)
            .digest("hex");

        if (!crypto.timingSafeEqual(
            Buffer.from(signature),
            Buffer.from(expected)
        )) {
            throw new Error("Invalid signature");
        }

        next();

    } catch (error) {

        return res.status(401).json({
            success: false,
            message: "Invalid or expired admin token"
        });

    }
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


// ============================================================
// ForiChi Map API
// دریافت خدمات نزدیک یک موقعیت جغرافیایی
// ============================================================

app.get("/api/map/services", async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        const latitude = Number(req.query.latitude);
        const longitude = Number(req.query.longitude);
        const radius = Number(req.query.radius || 20);

        if (
            !Number.isFinite(latitude) ||
            latitude < -90 ||
            latitude > 90
        ) {
            return res.status(400).json({
                success: false,
                message: "latitude نامعتبر است"
            });
        }

        if (
            !Number.isFinite(longitude) ||
            longitude < -180 ||
            longitude > 180
        ) {
            return res.status(400).json({
                success: false,
                message: "longitude نامعتبر است"
            });
        }

        const safeRadius =
            Number.isFinite(radius)
                ? Math.min(Math.max(radius, 1), 100)
                : 20;

        const { data: services, error } = await supabase
            .from("services")
            .select(`
                id,
                specialist_id,
                title,
                category,
                subcategory,
                description,
                price,
                price_type,
                city,
                latitude,
                longitude,
                specialists (
                    id,
                    name,
                    specialty,
                    city,
                    status,
                    is_active
                )
            `)
            .eq("is_active", true)
            .not("latitude", "is", null)
            .not("longitude", "is", null);

        if (error) {
            console.error("Map services error:", error);

            return res.status(500).json({
                success: false,
                message: "خطا در دریافت خدمات نقشه"
            });
        }

        const earthRadiusKm = 6371;

        const toRadians = value =>
            value * Math.PI / 180;

        const result = (services || [])
            .filter(service => {

                const specialist =
                    service.specialists;

                if (
                    !specialist ||
                    specialist.status !== "approved" ||
                    specialist.is_active !== true
                ) {
                    return false;
                }

                const lat1 =
                    toRadians(latitude);

                const lat2 =
                    toRadians(Number(service.latitude));

                const dLat =
                    toRadians(
                        Number(service.latitude) -
                        latitude
                    );

                const dLon =
                    toRadians(
                        Number(service.longitude) -
                        longitude
                    );

                const a =
                    Math.sin(dLat / 2) ** 2 +
                    Math.cos(lat1) *
                    Math.cos(lat2) *
                    Math.sin(dLon / 2) ** 2;

                const distance =
                    earthRadiusKm *
                    2 *
                    Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                    );

                service.distance_km =
                    Math.round(distance * 10) / 10;

                return distance <= safeRadius;

            })
            .sort(
                (a, b) =>
                    a.distance_km -
                    b.distance_km
            )
            .map(service => ({
                id: service.id,
                specialist_id: service.specialist_id,
                title: service.title,
                category: service.category,
                subcategory: service.subcategory,
                description: service.description,
                price: service.price,
                price_type: service.price_type,
                city: service.city,
                latitude: service.latitude,
                longitude: service.longitude,
                distance_km: service.distance_km,
                specialist: service.specialists
            }));

        return res.json({
            success: true,
            latitude,
            longitude,
            radius_km: safeRadius,
            count: result.length,
            services: result
        });

    } catch (error) {

        console.error("Map API error:", error);

        return res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});

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
// Admin Login
// ==================================================

app.post("/api/admin/login", (req, res) => {

    const { password } = req.body;

    if (!process.env.FORICHI_ADMIN_KEY) {
        return res.status(500).json({
            success: false,
            message: "Admin key is not configured"
        });
    }

    if (!password || password !== process.env.FORICHI_ADMIN_KEY) {
        return res.status(401).json({
            success: false,
            message: "رمز مدیر اشتباه است"
        });
    }

    const timestamp = String(Date.now());

    const signature = crypto
        .createHmac("sha256", process.env.FORICHI_ADMIN_KEY)
        .update(timestamp)
        .digest("hex");

    const token = Buffer
        .from(timestamp + "." + signature)
        .toString("base64url");

    res.json({
        success: true,
        message: "ورود مدیر موفق بود",
        token: token,
        expires_in: 86400
    });

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
// ForiChi - OTP + Specialists
// ==================================================

function normalizePhone(phone) {
    if (!phone) return null;

    let p = String(phone).trim()
        .replace(/[\s()-]/g, "");

    if (p.startsWith("+98")) {
        p = "0" + p.substring(3);
    } else if (p.startsWith("0098")) {
        p = "0" + p.substring(4);
    }

    return /^09\d{9}$/.test(p) ? p : null;
}

function hashOtp(code) {
    return crypto
        .createHash("sha256")
        .update(code)
        .digest("hex");
}

// --------------------------------------------------
// Send OTP
// --------------------------------------------------

app.post("/api/auth/send-code", async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        const phone = normalizePhone(req.body.phone);

        if (!phone) {
            return res.status(400).json({
                success: false,
                message: "شماره موبایل معتبر نیست"
            });
        }

        // جلوگیری از درخواست‌های پشت سر هم
        const since = new Date(Date.now() - 60 * 1000).toISOString();

        const { data: recent } = await supabase
            .from("otp_codes")
            .select("id")
            .eq("phone", phone)
            .gte("created_at", since)
            .limit(1);

        if (recent && recent.length > 0) {
            return res.status(429).json({
                success: false,
                message: "لطفاً ۶۰ ثانیه صبر کنید"
            });
        }

        const code = String(
            crypto.randomInt(100000, 1000000)
        );

        const codeHash = hashOtp(code);

        const expiresAt = new Date(
            Date.now() + 5 * 60 * 1000
        ).toISOString();

        const { error } = await supabase
            .from("otp_codes")
            .insert([{
                phone,
                code_hash: codeHash,
                expires_at: expiresAt,
                attempts: 0,
                used: false
            }]);

        if (error) {
            return res.status(500).json({
                success: false,
                message: "خطا در ذخیره کد تأیید",
                detail: error.message
            });
        }

        /*
         * فعلاً سرویس پیامک وصل نشده.
         *
         * در محیط production نباید OTP را در پاسخ API
         * برگردانیم.
         *
         * برای تست فعلی:
         */
        res.json({
            success: true,
            message: "کد تأیید ایجاد شد",
            expires_in: 300,
            dev_code: code
        });

    } catch (error) {

        console.error("OTP send error:", error);

        res.status(500).json({
            success: false,
            message: "خطای سرور"
        });

    }
});

// --------------------------------------------------
// Verify OTP
// --------------------------------------------------


function createSpecialistToken(phone) {
    const timestamp = String(Date.now());

    const signature = crypto
        .createHmac("sha256", process.env.FORICHI_SPECIALIST_KEY)
        .update(timestamp + "." + phone)
        .digest("hex");

    return Buffer
        .from(timestamp + "." + phone + "." + signature)
        .toString("base64url");
}

function verifySpecialistToken(token) {
    if (!token || !process.env.FORICHI_SPECIALIST_KEY) {
        return null;
    }

    try {
        const decoded = Buffer
            .from(token, "base64url")
            .toString("utf8");

        const parts = decoded.split(".");

        if (parts.length !== 3) {
            return null;
        }

        const [timestamp, phone, signature] = parts;

        const age = Date.now() - Number(timestamp);

        if (
            !timestamp ||
            !phone ||
            !signature ||
            !Number.isFinite(age) ||
            age < 0 ||
            age > 30 * 24 * 60 * 60 * 1000
        ) {
            return null;
        }

        const normalizedPhone = normalizePhone(phone);

        if (!normalizedPhone) {
            return null;
        }

        const expected = crypto
            .createHmac("sha256", process.env.FORICHI_SPECIALIST_KEY)
            .update(timestamp + "." + normalizedPhone)
            .digest("hex");

        if (
            signature.length !== expected.length ||
            !crypto.timingSafeEqual(
                Buffer.from(signature),
                Buffer.from(expected)
            )
        ) {
            return null;
        }

        return normalizedPhone;

    } catch (error) {
        return null;
    }
}

function requireSpecialist(req, res, next) {
    const auth = req.headers["authorization"];

    if (!auth || !auth.startsWith("Bearer ")) {
        return res.status(401).json({
            success: false,
            message: "ورود متخصص الزامی است"
        });
    }

    const phone = verifySpecialistToken(auth.substring(7));

    if (!phone) {
        return res.status(401).json({
            success: false,
            message: "توکن متخصص نامعتبر یا منقضی شده است"
        });
    }

    req.specialistPhone = phone;
    next();
}





app.get("/api/user/orders", requireUser, async (req, res) => {
    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {
        const { data: user, error: userError } = await supabase
            .from("users")
            .select("id")
            .eq("phone", req.userPhone)
            .maybeSingle();

        if (userError || !user) {
            return res.status(404).json({
                success: false,
                message: "کاربر پیدا نشد"
            });
        }

        const { data: orders, error } = await supabase
            .from("orders")
            .select(`
                id,
                user_id,
                specialist_id,
                service_id,
                status,
                description,
                address,
                city,
                price,
                payment_status,
                created_at,
                updated_at
            `)
            .eq("user_id", user.id)
            .order("created_at", { ascending: false });

        if (error) {
            return res.status(500).json({
                success: false,
                message: "خطا در دریافت سفارش‌ها"
            });
        }

        return res.json({
            success: true,
            orders: orders || []
        });

    } catch (error) {
        console.error("User orders GET error:", error);

        return res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});


app.get("/api/user/orders/:id", requireUser, async (req, res) => {
    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {
        const orderId = Number(req.params.id);

        if (!Number.isInteger(orderId) || orderId <= 0) {
            return res.status(400).json({
                success: false,
                message: "شناسه سفارش معتبر نیست"
            });
        }

        const { data: user, error: userError } = await supabase
            .from("users")
            .select("id")
            .eq("phone", req.userPhone)
            .maybeSingle();

        if (userError || !user) {
            return res.status(404).json({
                success: false,
                message: "کاربر پیدا نشد"
            });
        }

        const { data: order, error } = await supabase
            .from("orders")
            .select(`
                id,
                user_id,
                specialist_id,
                service_id,
                status,
                description,
                address,
                city,
                price,
                payment_status,
                created_at,
                updated_at
            `)
            .eq("id", orderId)
            .eq("user_id", user.id)
            .maybeSingle();

        if (error) {
            return res.status(500).json({
                success: false,
                message: "خطا در دریافت سفارش"
            });
        }

        if (!order) {
            return res.status(404).json({
                success: false,
                message: "سفارش پیدا نشد"
            });
        }

        return res.json({
            success: true,
            order: order
        });

    } catch (error) {
        console.error("User order detail error:", error);

        return res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});


app.patch("/api/user/orders/:id/cancel", requireUser, async (req, res) => {
    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {
        const orderId = Number(req.params.id);

        if (!Number.isInteger(orderId) || orderId <= 0) {
            return res.status(400).json({
                success: false,
                message: "شناسه سفارش معتبر نیست"
            });
        }

        const { data: user, error: userError } = await supabase
            .from("users")
            .select("id")
            .eq("phone", req.userPhone)
            .maybeSingle();

        if (userError || !user) {
            return res.status(404).json({
                success: false,
                message: "کاربر پیدا نشد"
            });
        }

        const { data: order, error: orderError } = await supabase
            .from("orders")
            .select("id, status, payment_status")
            .eq("id", orderId)
            .eq("user_id", user.id)
            .maybeSingle();

        if (orderError) {
            return res.status(500).json({
                success: false,
                message: "خطا در دریافت سفارش"
            });
        }

        if (!order) {
            return res.status(404).json({
                success: false,
                message: "سفارش پیدا نشد"
            });
        }

        if (order.status !== "pending") {
            return res.status(400).json({
                success: false,
                message: "فقط سفارش در انتظار تأیید قابل لغو است"
            });
        }

        const { data: updatedOrder, error: updateError } = await supabase
            .from("orders")
            .update({
                status: "cancelled",
                updated_at: new Date().toISOString()
            })
            .eq("id", orderId)
            .eq("user_id", user.id)
            .select("*")
            .single();

        if (updateError) {
            return res.status(500).json({
                success: false,
                message: "خطا در لغو سفارش"
            });
        }

        return res.json({
            success: true,
            message: "سفارش لغو شد",
            order: updatedOrder
        });

    } catch (error) {
        console.error("User order cancel error:", error);

        return res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});

app.post("/api/user/reviews", requireUser, async (req, res) => {
    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {
        const orderId = Number(req.body.order_id);
        const rating = Number(req.body.rating);

        const comment =
            typeof req.body.comment === "string"
                ? req.body.comment.trim()
                : null;

        if (!Number.isInteger(orderId) || orderId <= 0) {
            return res.status(400).json({
                success: false,
                message: "شناسه سفارش معتبر نیست"
            });
        }

        if (!Number.isInteger(rating) || rating < 1 || rating > 5) {
            return res.status(400).json({
                success: false,
                message: "امتیاز باید بین ۱ تا ۵ باشد"
            });
        }

        if (comment && comment.length > 1000) {
            return res.status(400).json({
                success: false,
                message: "متن نظر بیش از حد طولانی است"
            });
        }

        const { data: user, error: userError } = await supabase
            .from("users")
            .select("id")
            .eq("phone", req.userPhone)
            .maybeSingle();

        if (userError || !user) {
            return res.status(404).json({
                success: false,
                message: "کاربر پیدا نشد"
            });
        }

        const { data: order, error: orderError } = await supabase
            .from("orders")
            .select("id, user_id, specialist_id, status")
            .eq("id", orderId)
            .eq("user_id", user.id)
            .maybeSingle();

        if (orderError) {
            return res.status(500).json({
                success: false,
                message: "خطا در دریافت سفارش"
            });
        }

        if (!order) {
            return res.status(404).json({
                success: false,
                message: "سفارش پیدا نشد"
            });
        }

        if (order.status !== "completed") {
            return res.status(400).json({
                success: false,
                message: "فقط سفارش تکمیل‌شده قابل امتیازدهی است"
            });
        }

        const { data: existingReview, error: existingError } = await supabase
            .from("reviews")
            .select("id")
            .eq("order_id", order.id)
            .maybeSingle();

        if (existingError) {
            return res.status(500).json({
                success: false,
                message: "خطا در بررسی امتیاز قبلی"
            });
        }

        if (existingReview) {
            return res.status(409).json({
                success: false,
                message: "این سفارش قبلاً امتیازدهی شده است"
            });
        }

        const { data: review, error: reviewError } = await supabase
            .from("reviews")
            .insert({
                order_id: order.id,
                user_id: user.id,
                specialist_id: order.specialist_id,
                rating,
                comment: comment || null
            })
            .select("*")
            .single();

        if (reviewError) {
            return res.status(500).json({
                success: false,
                message: "خطا در ثبت امتیاز"
            });
        }

        return res.status(201).json({
            success: true,
            message: "امتیاز و نظر با موفقیت ثبت شد",
            review
        });

    } catch (error) {
        console.error("Create review error:", error);

        return res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});

app.post("/api/user/orders", requireUser, async (req, res) => {
    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {
        const serviceId = Number(req.body.service_id);
        const description =
            typeof req.body.description === "string"
                ? req.body.description.trim()
                : null;
        const address =
            typeof req.body.address === "string"
                ? req.body.address.trim()
                : null;
        const city =
            typeof req.body.city === "string"
                ? req.body.city.trim()
                : null;

        if (!Number.isInteger(serviceId) || serviceId <= 0) {
            return res.status(400).json({
                success: false,
                message: "شناسه خدمت معتبر نیست"
            });
        }

        if (description && description.length > 1000) {
            return res.status(400).json({
                success: false,
                message: "توضیحات بیش از حد طولانی است"
            });
        }

        if (address && address.length > 500) {
            return res.status(400).json({
                success: false,
                message: "آدرس بیش از حد طولانی است"
            });
        }

        if (city && city.length > 100) {
            return res.status(400).json({
                success: false,
                message: "نام شهر بیش از حد طولانی است"
            });
        }

        const { data: user, error: userError } = await supabase
            .from("users")
            .select("id, phone, name, city, is_active")
            .eq("phone", req.userPhone)
            .maybeSingle();

        if (userError || !user) {
            return res.status(404).json({
                success: false,
                message: "کاربر پیدا نشد"
            });
        }

        if (!user.is_active) {
            return res.status(403).json({
                success: false,
                message: "حساب کاربر غیرفعال است"
            });
        }

        const { data: service, error: serviceError } = await supabase
            .from("services")
            .select(`
                id,
                specialist_id,
                title,
                category,
                description,
                price,
                price_type,
                city,
                is_active
            `)
            .eq("id", serviceId)
            .maybeSingle();

        if (serviceError) {
            return res.status(500).json({
                success: false,
                message: "خطا در دریافت خدمت"
            });
        }

        if (!service) {
            return res.status(404).json({
                success: false,
                message: "خدمت پیدا نشد"
            });
        }

        if (!service.is_active) {
            return res.status(400).json({
                success: false,
                message: "این خدمت در حال حاضر فعال نیست"
            });
        }

        const { data: specialist, error: specialistError } = await supabase
            .from("specialists")
            .select("id, phone, name, specialty, city, status, is_active")
            .eq("id", service.specialist_id)
            .maybeSingle();

        if (specialistError || !specialist) {
            return res.status(404).json({
                success: false,
                message: "متخصص این خدمت پیدا نشد"
            });
        }

        if (
            specialist.status !== "approved" ||
            specialist.is_active !== true
        ) {
            return res.status(400).json({
                success: false,
                message: "متخصص این خدمت در حال حاضر فعال نیست"
            });
        }

        const orderCity = city || user.city || service.city || specialist.city || null;

        const { data: order, error: orderError } = await supabase
            .from("orders")
            .insert([{
                user_id: user.id,
                specialist_id: specialist.id,
                service_id: service.id,
                status: "pending",
                description: description,
                address: address,
                city: orderCity,
                price: service.price,
                payment_status: "unpaid"
            }])
            .select("*")
            .single();

        if (orderError) {
            console.error("Order create error:", orderError);

            return res.status(500).json({
                success: false,
                message: "خطا در ثبت سفارش"
            });
        }

        return res.status(201).json({
            success: true,
            message: "سفارش با موفقیت ثبت شد",
            order: order,
            service: service,
            specialist: specialist
        });

    } catch (error) {
        console.error("User order create error:", error);

        return res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});

app.get("/api/user/profile", requireUser, async (req, res) => {
    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {
        const { data: user, error } = await supabase
            .from("users")
            .select("*")
            .eq("phone", req.userPhone)
            .maybeSingle();

        if (error) {
            return res.status(500).json({
                success: false,
                message: "خطا در دریافت پروفایل"
            });
        }

        if (!user) {
            return res.status(404).json({
                success: false,
                message: "کاربر پیدا نشد"
            });
        }

        return res.json({
            success: true,
            user: user
        });

    } catch (error) {
        console.error("User profile GET error:", error);

        return res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});


app.put("/api/user/profile", requireUser, async (req, res) => {
    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {
        const name =
            typeof req.body.name === "string"
                ? req.body.name.trim()
                : undefined;

        const city =
            typeof req.body.city === "string"
                ? req.body.city.trim()
                : undefined;

        const avatarUrl =
            typeof req.body.avatar_url === "string"
                ? req.body.avatar_url.trim()
                : undefined;

        if (name !== undefined && name.length > 100) {
            return res.status(400).json({
                success: false,
                message: "نام بیش از حد طولانی است"
            });
        }

        if (city !== undefined && city.length > 100) {
            return res.status(400).json({
                success: false,
                message: "نام شهر بیش از حد طولانی است"
            });
        }

        if (avatarUrl !== undefined && avatarUrl.length > 1000) {
            return res.status(400).json({
                success: false,
                message: "آدرس تصویر بیش از حد طولانی است"
            });
        }

        const updates = {
            updated_at: new Date().toISOString()
        };

        if (name !== undefined) {
            updates.name = name || null;
        }

        if (city !== undefined) {
            updates.city = city || null;
        }

        if (avatarUrl !== undefined) {
            updates.avatar_url = avatarUrl || null;
        }

        const { data: user, error } = await supabase
            .from("users")
            .update(updates)
            .eq("phone", req.userPhone)
            .select("*")
            .single();

        if (error) {
            return res.status(500).json({
                success: false,
                message: "خطا در بروزرسانی پروفایل"
            });
        }

        return res.json({
            success: true,
            message: "پروفایل با موفقیت بروزرسانی شد",
            user: user
        });

    } catch (error) {
        console.error("User profile PUT error:", error);

        return res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});

app.post("/api/user/auth/verify-code", async (req, res) => {
    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {
        const phone = normalizePhone(req.body.phone);
        const code = String(req.body.code || "").trim();

        if (!phone) {
            return res.status(400).json({
                success: false,
                message: "شماره موبایل معتبر نیست"
            });
        }

        if (!/^\d{6}$/.test(code)) {
            return res.status(400).json({
                success: false,
                message: "کد تأیید باید ۶ رقمی باشد"
            });
        }

        const { data: otpRows, error: otpError } = await supabase
            .from("otp_codes")
            .select("*")
            .eq("phone", phone)
            .eq("used", false)
            .order("created_at", { ascending: false })
            .limit(1);

        if (otpError) {
            return res.status(500).json({
                success: false,
                message: "خطا در بررسی کد تأیید"
            });
        }

        if (!otpRows || otpRows.length === 0) {
            return res.status(400).json({
                success: false,
                message: "کد تأیید پیدا نشد"
            });
        }

        const otp = otpRows[0];

        if (new Date(otp.expires_at).getTime() < Date.now()) {
            return res.status(400).json({
                success: false,
                message: "کد تأیید منقضی شده است"
            });
        }

        if (Number(otp.attempts) >= 5) {
            return res.status(429).json({
                success: false,
                message: "تعداد تلاش‌های مجاز تمام شده است"
            });
        }

        if (hashOtp(code) !== otp.code_hash) {
            await supabase
                .from("otp_codes")
                .update({
                    attempts: Number(otp.attempts) + 1
                })
                .eq("id", otp.id);

            return res.status(400).json({
                success: false,
                message: "کد تأیید اشتباه است"
            });
        }

        const { error: usedError } = await supabase
            .from("otp_codes")
            .update({ used: true })
            .eq("id", otp.id);

        if (usedError) {
            return res.status(500).json({
                success: false,
                message: "خطا در ثبت تأیید کد"
            });
        }

        const { data: existingUser, error: findError } = await supabase
            .from("users")
            .select("*")
            .eq("phone", phone)
            .maybeSingle();

        if (findError) {
            return res.status(500).json({
                success: false,
                message: "خطا در بررسی حساب کاربر"
            });
        }

        let user = existingUser;

        if (!user) {
            const { data: newUser, error: createError } = await supabase
                .from("users")
                .insert([{
                    phone: phone,
                    is_active: true
                }])
                .select("*")
                .single();

            if (createError) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در ساخت حساب کاربر"
                });
            }

            user = newUser;
        } else if (!user.is_active) {
            const { data: activatedUser, error: activateError } = await supabase
                .from("users")
                .update({
                    is_active: true,
                    updated_at: new Date().toISOString()
                })
                .eq("id", user.id)
                .select("*")
                .single();

            if (activateError) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در فعال‌سازی حساب کاربر"
                });
            }

            user = activatedUser;
        }

        const token = createUserToken(phone);

        return res.json({
            success: true,
            message: "ورود کاربر موفق بود",
            phone: phone,
            token: token,
            expires_in: 2592000,
            user: user
        });

    } catch (error) {
        console.error("User OTP verify error:", error);

        return res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});

app.post("/api/auth/verify-code", async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        const phone = normalizePhone(req.body.phone);
        const code = String(req.body.code || "").trim();

        if (!phone) {
            return res.status(400).json({
                success: false,
                message: "شماره موبایل معتبر نیست"
            });
        }

        if (!/^\d{6}$/.test(code)) {
            return res.status(400).json({
                success: false,
                message: "کد تأیید باید ۶ رقمی باشد"
            });
        }

        const { data: otp, error } = await supabase
            .from("otp_codes")
            .select("*")
            .eq("phone", phone)
            .eq("used", false)
            .order("created_at", { ascending: false })
            .limit(1)
            .maybeSingle();

        if (error) {
            return res.status(500).json({
                success: false,
                message: "خطا در بررسی کد",
                detail: error.message
            });
        }

        if (!otp) {
            return res.status(400).json({
                success: false,
                message: "کد تأیید پیدا نشد یا قبلاً استفاده شده"
            });
        }

        if (new Date(otp.expires_at).getTime() < Date.now()) {
            return res.status(400).json({
                success: false,
                message: "کد تأیید منقضی شده است"
            });
        }

        if (otp.attempts >= 5) {
            return res.status(429).json({
                success: false,
                message: "تعداد تلاش بیش از حد مجاز است"
            });
        }

        const incomingHash = hashOtp(code);

        if (incomingHash !== otp.code_hash) {

            await supabase
                .from("otp_codes")
                .update({
                    attempts: otp.attempts + 1
                })
                .eq("id", otp.id);

            return res.status(401).json({
                success: false,
                message: "کد تأیید اشتباه است"
            });
        }

        await supabase
            .from("otp_codes")
            .update({
                used: true
            })
            .eq("id", otp.id);

        const token = createSpecialistToken(phone);

        res.json({
            success: true,
            message: "شماره موبایل با موفقیت تأیید شد",
            phone,
            token,
            expires_in: 30 * 24 * 60 * 60
        });

    } catch (error) {

        console.error("OTP verify error:", error);

        res.status(500).json({
            success: false,
            message: "خطای سرور"
        });

    }
});

// --------------------------------------------------
// Specialist Register
// --------------------------------------------------


// دریافت اطلاعات عمومی متخصص برای نمایش در اپ
app.get("/api/specialists/:id", async (req, res) => {
    try {
        const specialistId = Number(req.params.id);

        if (!Number.isInteger(specialistId) || specialistId <= 0) {
            return res.status(400).json({
                success: false,
                message: "شناسه متخصص نامعتبر است"
            });
        }

        const { data, error } = await supabase
            .from("specialists")
            .select("id, name, specialty, city, description, status, is_active")
            .eq("id", specialistId)
            .eq("status", "approved")
            .eq("is_active", true)
            .maybeSingle();

        if (error) {
            console.error("Public specialist error:", error);

            return res.status(500).json({
                success: false,
                message: "خطا در دریافت اطلاعات متخصص"
            });
        }

        if (!data) {
            return res.status(404).json({
                success: false,
                message: "متخصص موردنظر پیدا نشد"
            });
        }

        res.json({
            success: true,
            specialist: data
        });

    } catch (error) {
        console.error("Public specialist route error:", error);

        res.status(500).json({
            success: false,
            message: "خطای سرور"
        });
    }
});

app.post("/api/specialists/register", requireSpecialist, async (req, res) => {

    if (!supabase) {
        return res.status(500).json({
            success: false,
            message: "Supabase is not configured"
        });
    }

    try {

        const phone = req.specialistPhone;

        const {
            name,
            specialty,
            city,
            description
        } = req.body;

        if (!name || !String(name).trim()) {
            return res.status(400).json({
                success: false,
                message: "نام الزامی است"
            });
        }

        const { data: existing, error: existingError } = await supabase
            .from("specialists")
            .select("id,status")
            .eq("phone", phone)
            .maybeSingle();

        if (existingError) {
            return res.status(500).json({
                success: false,
                message: "Database error",
                detail: existingError.message
            });
        }

        if (existing) {
            return res.status(409).json({
                success: false,
                message: "این شماره قبلاً به عنوان متخصص ثبت شده است",
                status: existing.status
            });
        }

        const { data, error } = await supabase
            .from("specialists")
            .insert([{
                phone,
                name: String(name).trim(),
                specialty: specialty ? String(specialty).trim() : null,
                city: city ? String(city).trim() : null,
                description: description ? String(description).trim() : null,
                status: "pending",
                is_active: false
            }])
            .select()
            .single();

        if (error) {
            console.error("Specialist register error:", error);

            return res.status(500).json({
                success: false,
                message: "ثبت متخصص انجام نشد",
                detail: error.message
            });
        }

        return res.status(201).json({
            success: true,
            message: "درخواست ثبت متخصص با موفقیت ارسال شد و در انتظار تأیید مدیر است",
            specialist: data
        });

    } catch (error) {

        console.error("Specialist register error:", error);

        return res.status(500).json({
            success: false,
            message: "Server error"
        });
    }
});

// --------------------------------------------------
// Admin: List Specialists
// --------------------------------------------------

app.get(
    "/api/admin/specialists",
    requireAdmin,
    async (req, res) => {

        try {

            const status = req.query.status;

            let query = supabase
                .from("specialists")
                .select("*")
                .order("created_at", { ascending: false });

            if (
                status &&
                ["pending", "approved", "rejected"].includes(status)
            ) {
                query = query.eq("status", status);
            }

            const { data, error } = await query;

            if (error) {
                console.error("Admin specialists error:", error);

                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت متخصص‌ها",
                    detail: error.message
                });
            }

            res.json({
                success: true,
                count: data.length,
                specialists: data
            });

        } catch (error) {

            console.error("Admin specialists error:", error);

            res.status(500).json({
                success: false,
                message: "Server error"
            });
        }
    }
);

// --------------------------------------------------
// Specialist: Services API
// --------------------------------------------------

app.get(
    "/api/specialist/services",
    requireSpecialist,
    async (req, res) => {

        try {

            const { data: specialist, error: specialistError } = await supabase
                .from("specialists")
                .select("id,status,is_active")
                .eq("phone", req.specialistPhone)
                .maybeSingle();

            if (specialistError) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت متخصص",
                    detail: specialistError.message
                });
            }

            if (!specialist) {
                return res.status(404).json({
                    success: false,
                    message: "متخصص پیدا نشد"
                });
            }

            if (specialist.status !== "approved" || !specialist.is_active) {
                return res.status(403).json({
                    success: false,
                    message: "حساب متخصص هنوز فعال نشده است"
                });
            }

            const { data, error } = await supabase
                .from("services")
                .select("*")
                .eq("specialist_id", specialist.id)
                .order("id", { ascending: false });

            if (error) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت خدمات",
                    detail: error.message
                });
            }

            res.json({
                success: true,
                count: data.length,
                services: data
            });

        } catch (error) {

            console.error("Specialist services error:", error);

            res.status(500).json({
                success: false,
                message: "Server error"
            });
        }
    }
);


app.post(
    "/api/specialist/services",
    requireSpecialist,
    async (req, res) => {

        try {

            const {
                title,
                category,
                subcategory,
                description,
                price,
                price_type,
                city
            } = req.body;

            if (!title || !String(title).trim()) {
                return res.status(400).json({
                    success: false,
                    message: "عنوان خدمت الزامی است"
                });
            }

            const { data: specialist, error: specialistError } = await supabase
                .from("specialists")
                .select("id,status,is_active")
                .eq("phone", req.specialistPhone)
                .maybeSingle();

            if (specialistError) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت متخصص",
                    detail: specialistError.message
                });
            }

            if (!specialist) {
                return res.status(404).json({
                    success: false,
                    message: "متخصص پیدا نشد"
                });
            }

            if (specialist.status !== "approved" || !specialist.is_active) {
                return res.status(403).json({
                    success: false,
                    message: "حساب متخصص هنوز فعال نشده است"
                });
            }

            const allowedPriceTypes = ["fixed", "starting", "negotiable"];

            const finalPriceType =
                allowedPriceTypes.includes(price_type)
                    ? price_type
                    : "fixed";

            let finalPrice = null;

            if (price !== undefined && price !== null && price !== "") {
                finalPrice = Number(price);

                if (!Number.isFinite(finalPrice) || finalPrice < 0) {
                    return res.status(400).json({
                        success: false,
                        message: "قیمت نامعتبر است"
                    });
                }
            }

            const { data, error } = await supabase
                .from("services")
                .insert([{
                    specialist_id: specialist.id,
                    title: String(title).trim(),
                    category: category ? String(category).trim() : null,
                    subcategory: subcategory ? String(subcategory).trim() : null,
                    description: description ? String(description).trim() : null,
                    price: finalPrice,
                    price_type: finalPriceType,
                    city: city ? String(city).trim() : null,
                    is_active: true
                }])
                .select()
                .single();

            if (error) {
                console.error("Create service error:", error);

                return res.status(500).json({
                    success: false,
                    message: "ثبت خدمت انجام نشد",
                    detail: error.message
                });
            }

            res.status(201).json({
                success: true,
                message: "خدمت با موفقیت ثبت شد",
                service: data
            });

        } catch (error) {

            console.error("Create service error:", error);

            res.status(500).json({
                success: false,
                message: "Server error"
            });
        }
    }
);


app.put(
    "/api/specialist/services/:id",
    requireSpecialist,
    async (req, res) => {

        try {

            const serviceId = req.params.id;

            const { data: specialist, error: specialistError } = await supabase
                .from("specialists")
                .select("id,status,is_active")
                .eq("phone", req.specialistPhone)
                .maybeSingle();

            if (specialistError) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت متخصص",
                    detail: specialistError.message
                });
            }

            if (!specialist) {
                return res.status(404).json({
                    success: false,
                    message: "متخصص پیدا نشد"
                });
            }

            if (specialist.status !== "approved" || !specialist.is_active) {
                return res.status(403).json({
                    success: false,
                    message: "حساب متخصص هنوز فعال نشده است"
                });
            }

            const {
                title,
                category,
                subcategory,
                description,
                price,
                price_type,
                city
            } = req.body;

            const updates = {
                updated_at: new Date().toISOString()
            };

            if (title !== undefined) {
                if (!String(title).trim()) {
                    return res.status(400).json({
                        success: false,
                        message: "عنوان خدمت نمی‌تواند خالی باشد"
                    });
                }

                updates.title = String(title).trim();
            }

            if (category !== undefined) {
                updates.category = category ? String(category).trim() : null;
            }

            if (subcategory !== undefined) {
                updates.subcategory = subcategory ? String(subcategory).trim() : null;
            }

            if (description !== undefined) {
                updates.description = description ? String(description).trim() : null;
            }

            if (city !== undefined) {
                updates.city = city ? String(city).trim() : null;
            }

            if (price !== undefined) {

                if (price === null || price === "") {
                    updates.price = null;

                } else {

                    const finalPrice = Number(price);

                    if (!Number.isFinite(finalPrice) || finalPrice < 0) {
                        return res.status(400).json({
                            success: false,
                            message: "قیمت نامعتبر است"
                        });
                    }

                    updates.price = finalPrice;
                }
            }

            if (price_type !== undefined) {

                if (!["fixed", "starting", "negotiable"].includes(price_type)) {
                    return res.status(400).json({
                        success: false,
                        message: "نوع قیمت نامعتبر است"
                    });
                }

                updates.price_type = price_type;
            }

            const { data, error } = await supabase
                .from("services")
                .update(updates)
                .eq("id", serviceId)
                .eq("specialist_id", specialist.id)
                .select()
                .maybeSingle();

            if (error) {
                return res.status(500).json({
                    success: false,
                    message: "ویرایش خدمت انجام نشد",
                    detail: error.message
                });
            }

            if (!data) {
                return res.status(404).json({
                    success: false,
                    message: "خدمت پیدا نشد یا متعلق به شما نیست"
                });
            }

            res.json({
                success: true,
                message: "خدمت با موفقیت ویرایش شد",
                service: data
            });

        } catch (error) {

            console.error("Update service error:", error);

            res.status(500).json({
                success: false,
                message: "Server error"
            });
        }
    }
);


app.delete(
    "/api/specialist/services/:id",
    requireSpecialist,
    async (req, res) => {

        try {

            const { data: specialist, error: specialistError } = await supabase
                .from("specialists")
                .select("id,status,is_active")
                .eq("phone", req.specialistPhone)
                .maybeSingle();

            if (specialistError) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت متخصص",
                    detail: specialistError.message
                });
            }

            if (!specialist) {
                return res.status(404).json({
                    success: false,
                    message: "متخصص پیدا نشد"
                });
            }

            const { data, error } = await supabase
                .from("services")
                .delete()
                .eq("id", req.params.id)
                .eq("specialist_id", specialist.id)
                .select()
                .maybeSingle();

            if (error) {
                return res.status(500).json({
                    success: false,
                    message: "حذف خدمت انجام نشد",
                    detail: error.message
                });
            }

            if (!data) {
                return res.status(404).json({
                    success: false,
                    message: "خدمت پیدا نشد یا متعلق به شما نیست"
                });
            }

            res.json({
                success: true,
                message: "خدمت حذف شد"
            });

        } catch (error) {

            console.error("Delete service error:", error);

            res.status(500).json({
                success: false,
                message: "Server error"
            });
        }
    }
);


app.patch(
    "/api/specialist/services/:id/status",
    requireSpecialist,
    async (req, res) => {

        try {

            const { is_active } = req.body;

            if (typeof is_active !== "boolean") {
                return res.status(400).json({
                    success: false,
                    message: "is_active باید true یا false باشد"
                });
            }

            const { data: specialist, error: specialistError } = await supabase
                .from("specialists")
                .select("id,status,is_active")
                .eq("phone", req.specialistPhone)
                .maybeSingle();

            if (specialistError) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت متخصص",
                    detail: specialistError.message
                });
            }

            if (!specialist) {
                return res.status(404).json({
                    success: false,
                    message: "متخصص پیدا نشد"
                });
            }

            if (specialist.status !== "approved" || !specialist.is_active) {
                return res.status(403).json({
                    success: false,
                    message: "حساب متخصص هنوز فعال نشده است"
                });
            }

            const { data, error } = await supabase
                .from("services")
                .update({
                    is_active,
                    updated_at: new Date().toISOString()
                })
                .eq("id", req.params.id)
                .eq("specialist_id", specialist.id)
                .select()
                .maybeSingle();

            if (error) {
                return res.status(500).json({
                    success: false,
                    message: "تغییر وضعیت خدمت انجام نشد",
                    detail: error.message
                });
            }

            if (!data) {
                return res.status(404).json({
                    success: false,
                    message: "خدمت پیدا نشد یا متعلق به شما نیست"
                });
            }

            res.json({
                success: true,
                message: is_active ? "خدمت فعال شد" : "خدمت غیرفعال شد",
                service: data
            });

        } catch (error) {

            console.error("Service status error:", error);

            res.status(500).json({
                success: false,
                message: "Server error"
            });
        }
    }
);

// --------------------------------------------------
// Approve Specialist
// --------------------------------------------------

app.patch(
    "/api/admin/specialists/:id/approve",
    requireAdmin,
    async (req, res) => {

        try {

            const { id } = req.params;

            const { data, error } = await supabase
                .from("specialists")
                .update({
                    status: "approved",
                    is_active: true,
                    updated_at: new Date().toISOString()
                })
                .eq("id", id)
                .select()
                .single();

            if (error) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در تأیید متخصص",
                    detail: error.message
                });
            }

            res.json({
                success: true,
                message: "متخصص تأیید شد",
                specialist: data
            });

        } catch (error) {

            res.status(500).json({
                success: false,
                message: "Server error"
            });

        }
    }
);

// --------------------------------------------------
// Reject Specialist
// --------------------------------------------------

app.patch(
    "/api/admin/specialists/:id/reject",
    requireAdmin,
    async (req, res) => {

        try {

            const { id } = req.params;

            const { data, error } = await supabase
                .from("specialists")
                .update({
                    status: "rejected",
                    is_active: false,
                    updated_at: new Date().toISOString()
                })
                .eq("id", id)
                .select()
                .single();

            if (error) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در رد متخصص",
                    detail: error.message
                });
            }

            res.json({
                success: true,
                message: "متخصص رد شد",
                specialist: data
            });

        } catch (error) {

            res.status(500).json({
                success: false,
                message: "Server error"
            });

        }
    }
);

// --------------------------------------------------

// --------------------------------------------------
// Specialist Orders
// --------------------------------------------------

app.get(
    "/api/specialist/orders",
    requireSpecialist,
    async (req, res) => {
        if (!supabase) {
            return res.status(500).json({
                success: false,
                message: "Supabase is not configured"
            });
        }

        try {
            const { data: specialist, error: specialistError } =
                await supabase
                    .from("specialists")
                    .select("id")
                    .eq("phone", req.specialistPhone)
                    .maybeSingle();

            if (specialistError || !specialist) {
                return res.status(404).json({
                    success: false,
                    message: "متخصص پیدا نشد"
                });
            }

            const { data: orders, error } = await supabase
                .from("orders")
                .select("*")
                .eq("specialist_id", specialist.id)
                .order("created_at", { ascending: false });

            if (error) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت سفارش‌ها"
                });
            }

            return res.json({
                success: true,
                orders: orders || []
            });

        } catch (error) {
            console.error("Specialist orders GET error:", error);

            return res.status(500).json({
                success: false,
                message: "خطای سرور"
            });
        }
    }
);


app.get(
    "/api/specialist/orders/:id",
    requireSpecialist,
    async (req, res) => {
        if (!supabase) {
            return res.status(500).json({
                success: false,
                message: "Supabase is not configured"
            });
        }

        try {
            const orderId = Number(req.params.id);

            if (!Number.isInteger(orderId) || orderId <= 0) {
                return res.status(400).json({
                    success: false,
                    message: "شناسه سفارش معتبر نیست"
                });
            }

            const { data: specialist, error: specialistError } =
                await supabase
                    .from("specialists")
                    .select("id")
                    .eq("phone", req.specialistPhone)
                    .maybeSingle();

            if (specialistError || !specialist) {
                return res.status(404).json({
                    success: false,
                    message: "متخصص پیدا نشد"
                });
            }

            const { data: order, error } = await supabase
                .from("orders")
                .select("*")
                .eq("id", orderId)
                .eq("specialist_id", specialist.id)
                .maybeSingle();

            if (error) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت سفارش"
                });
            }

            if (!order) {
                return res.status(404).json({
                    success: false,
                    message: "سفارش پیدا نشد"
                });
            }

            return res.json({
                success: true,
                order: order
            });

        } catch (error) {
            console.error("Specialist order detail error:", error);

            return res.status(500).json({
                success: false,
                message: "خطای سرور"
            });
        }
    }
);


app.patch(
    "/api/specialist/orders/:id/status",
    requireSpecialist,
    async (req, res) => {
        if (!supabase) {
            return res.status(500).json({
                success: false,
                message: "Supabase is not configured"
            });
        }

        try {
            const orderId = Number(req.params.id);
            const newStatus = String(req.body.status || "").trim();

            const allowedStatuses = [
                "confirmed",
                "in_progress",
                "completed",
                "cancelled"
            ];

            if (!Number.isInteger(orderId) || orderId <= 0) {
                return res.status(400).json({
                    success: false,
                    message: "شناسه سفارش معتبر نیست"
                });
            }

            if (!allowedStatuses.includes(newStatus)) {
                return res.status(400).json({
                    success: false,
                    message: "وضعیت سفارش معتبر نیست"
                });
            }

            const { data: specialist, error: specialistError } =
                await supabase
                    .from("specialists")
                    .select("id, status, is_active")
                    .eq("phone", req.specialistPhone)
                    .maybeSingle();

            if (specialistError || !specialist) {
                return res.status(404).json({
                    success: false,
                    message: "متخصص پیدا نشد"
                });
            }

            if (
                specialist.status !== "approved" ||
                specialist.is_active !== true
            ) {
                return res.status(403).json({
                    success: false,
                    message: "متخصص فعال نیست"
                });
            }

            const { data: order, error: orderError } =
                await supabase
                    .from("orders")
                    .select("id, status")
                    .eq("id", orderId)
                    .eq("specialist_id", specialist.id)
                    .maybeSingle();

            if (orderError) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در دریافت سفارش"
                });
            }

            if (!order) {
                return res.status(404).json({
                    success: false,
                    message: "سفارش پیدا نشد"
                });
            }

            const transitions = {
                pending: ["confirmed", "cancelled"],
                confirmed: ["in_progress", "cancelled"],
                in_progress: ["completed", "cancelled"],
                completed: [],
                cancelled: []
            };

            if (!transitions[order.status]?.includes(newStatus)) {
                return res.status(400).json({
                    success: false,
                    message: "تغییر وضعیت مجاز نیست"
                });
            }

            const { data: updatedOrder, error: updateError } =
                await supabase
                    .from("orders")
                    .update({
                        status: newStatus,
                        updated_at: new Date().toISOString()
                    })
                    .eq("id", orderId)
                    .eq("specialist_id", specialist.id)
                    .select("*")
                    .single();

            if (updateError) {
                return res.status(500).json({
                    success: false,
                    message: "خطا در تغییر وضعیت سفارش"
                });
            }

            return res.json({
                success: true,
                message: "وضعیت سفارش بروزرسانی شد",
                order: updatedOrder
            });

        } catch (error) {
            console.error("Specialist order status error:", error);

            return res.status(500).json({
                success: false,
                message: "خطای سرور"
            });
        }
    }
);


// Specialist Profile
// --------------------------------------------------

app.get(
    "/api/specialist/profile",
    requireSpecialist,
    async (req, res) => {

        try {

            const phone = req.specialistPhone;

            const { data, error } = await supabase
                .from("specialists")
                .select("*")
                .eq("phone", phone)
                .maybeSingle();

            if (error) {
                return res.status(500).json({
                    success: false,
                    message: "Database error",
                    detail: error.message
                });
            }

            if (!data) {
                return res.status(404).json({
                    success: false,
                    message: "متخصص پیدا نشد"
                });
            }

            res.json({
                success: true,
                specialist: data
            });

        } catch (error) {

            console.error("Specialist profile error:", error);

            res.status(500).json({
                success: false,
                message: "Server error"
            });

        }
    }
);


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
