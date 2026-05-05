const Redis = require('ioredis');
const redis = new Redis({
  host: 'localhost',
  port: 6379
});

const DEFAULT_TTL = 60; 

async function get(key) {
  const data = await redis.get(key);
  return data ? JSON.parse(data) : null;
}

async function set(key, value, ttl = DEFAULT_TTL) {
  await redis.set(key, JSON.stringify(value), 'EX', ttl);
}

async function del(key) {
  await redis.del(key);
}

module.exports = { get, set, del };