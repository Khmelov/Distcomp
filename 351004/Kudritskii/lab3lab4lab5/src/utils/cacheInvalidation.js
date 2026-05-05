const redis = require("../db/redis");

exports.invalidateUser = async (id) => {
  await redis.del(`user:${id}`);
  await redis.del("users:all");
};
