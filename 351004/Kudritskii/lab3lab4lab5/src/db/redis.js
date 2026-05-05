const redis = require('redis');
const client = redis.createClient({ url: 'redis://localhost:6379' });
client.connect();
module.exports = client;
