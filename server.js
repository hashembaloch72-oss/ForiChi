const express = require("express");
const path = require("path");

const app = express();
const PORT = 3000;

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use(express.static(path.join(__dirname, "public")));

app.get("/", (req, res) => {
    res.sendFile(path.join(__dirname, "public", "index.html"));
});

// ==================================================
// ForiChi API
// ==================================================

app.get("/api/health", (req, res) => {
    res.json({
        success: true,
        app: "ForiChi",
        message: "API is running"
    });
});

app.get("/api", (req, res) => {
    res.json({
        success: true,
        message: "ForiChi API",
        version: "1.0"
    });
});

// ==================================================
// Admin API
// ==================================================

app.get("/api/admin/stats", (req, res) => {

    res.json({
        success: true,

        stats: {
            users: 0,
            specialists: 0,
            services: 0,
            orders: 0,
            balance: 0
        },

        message: "Admin API is ready"
    });
});

app.get("/api/admin/status", (req, res) => {

    res.json({
        success: true,
        admin_api: true,
        database: "pending",
        authentication: "pending"
    });
});

// ==================================================

app.listen(PORT, "0.0.0.0", () => {
    console.log(`ForiChi running on http://127.0.0.1:${PORT}`);
});
