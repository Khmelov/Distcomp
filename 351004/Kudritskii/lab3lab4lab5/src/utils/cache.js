const redis = require('../db/redis');

exports.getOrSet = async (key, ttl, cb) => {
  const cached = await redis.get(key);

  if (cached) {
    console.log('CACHE HIT');
    return JSON.parse(cached);
  }

  console.log('CACHE MISS');

  const data = await cb();

  if (data) {
    await redis.setEx(key, ttl, JSON.stringify(data));
  }

  return data;
};

exports.del = async (key) => {
  await redis.del(key);
};