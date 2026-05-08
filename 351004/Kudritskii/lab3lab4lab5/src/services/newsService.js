exports.getById = async (id) => ({
  id,
  title: "Sample News",
  content: "News content",
});

const pg = require("../db/postgres");
const redis = require("../db/redis");

exports.getById = async (id) => {
  const key = `user:${id}`;
  const cached = await redis.get(key);
  if (cached) return JSON.parse(cached);

  const result = await pg.query("SELECT * FROM tbl_users WHERE id=$1", [id]);
  const user = result.rows[0];
  if (user) await redis.setEx(key, 60, JSON.stringify(user));
  return user;
};

exports.getAll = async (id) => {
  const key = `user:${id}`;
  const cached = await redis.get(key);
  if (cached) return JSON.parse(cached);

  const result = await pg.query("SELECT * FROM tbl_users WHERE id=$1", [id]);
  const user = result.rows[0];
  if (user) await redis.setEx(key, 60, JSON.stringify(user));
  return user;
};
