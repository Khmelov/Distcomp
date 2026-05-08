const redis = require("../db/redis");

module.exports = async (req, res, next) => {
  const key = `rate:${req.ip}`;

  const requests = await redis.incr(key);

  if (requests === 1) {
    await redis.expire(key, 60);
  }

  if (requests > 100) {
    return res.status(429).json({ error: "Too many requests" });
  }

  next();
};
