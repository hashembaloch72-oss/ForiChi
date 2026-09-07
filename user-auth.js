const crypto = require("crypto");

function createUserToken(phone) {
    const timestamp = String(Date.now());

    const signature = crypto
        .createHmac("sha256", process.env.FORICHI_USER_KEY)
        .update(timestamp + "." + phone)
        .digest("hex");

    return Buffer
        .from(timestamp + "." + phone + "." + signature)
        .toString("base64url");
}

function verifyUserToken(token) {
    if (!token || !process.env.FORICHI_USER_KEY) {
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

        const expected = crypto
            .createHmac("sha256", process.env.FORICHI_USER_KEY)
            .update(timestamp + "." + phone)
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

        return phone;

    } catch (error) {
        return null;
    }
}

function requireUser(req, res, next) {
    const auth = req.headers["authorization"];

    if (!auth || !auth.startsWith("Bearer ")) {
        return res.status(401).json({
            success: false,
            message: "ورود کاربر الزامی است"
        });
    }

    const phone = verifyUserToken(auth.substring(7));

    if (!phone) {
        return res.status(401).json({
            success: false,
            message: "توکن کاربر نامعتبر یا منقضی شده است"
        });
    }

    req.userPhone = phone;
    next();
}

module.exports = {
    createUserToken,
    verifyUserToken,
    requireUser
};
