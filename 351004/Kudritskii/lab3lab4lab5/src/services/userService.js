const pg = require("../db/postgres");
const redis = require("../db/redis");

exports.getById = async (id) => {
  return cache.getOrSet(`user:${id}`, 60, async () => {
    const result = await pg.query("SELECT * FROM tbl_users WHERE id=$1", [id]);
    return result.rows[0];
  });
};
