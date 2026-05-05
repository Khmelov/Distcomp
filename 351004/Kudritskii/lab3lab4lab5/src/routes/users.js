const pg = require("../db/postgres");
const redis = require("../db/redis");

//
// 🔵 GET by ID (с кешем)
//
exports.getById = async (id) => {
  const key = `user:${id}`;

  const cached = await redis.get(key);

  if (cached) {
    console.log("CACHE HIT user");
    return JSON.parse(cached);
  }

  console.log("CACHE MISS user");

  const result = await pg.query("SELECT * FROM tbl_users WHERE id=$1", [id]);

  const user = result.rows[0];

  if (user) {
    await redis.setEx(key, 60, JSON.stringify(user));
  }

  return user;
};

//
// 🔵 GET ALL (с кешем)
//
exports.getAll = async () => {
  const key = "users:all";

  const cached = await redis.get(key);

  if (cached) {
    console.log("CACHE HIT users");
    return JSON.parse(cached);
  }

  console.log("CACHE MISS users");

  const result = await pg.query("SELECT * FROM tbl_users");

  await redis.setEx(key, 60, JSON.stringify(result.rows));

  return result.rows;
};

//
// 🟡 CREATE
//
exports.create = async (data) => {
  const result = await pg.query(
    "INSERT INTO tbl_users(name, lastname) VALUES($1,$2) RETURNING *",
    [data.name, data.lastname],
  );

  const user = result.rows[0];

  // ❗ чистим кеш списка
  await redis.del("users:all");

  return user;
};

//
// 🟡 UPDATE
//
exports.update = async (id, data) => {
  const result = await pg.query(
    "UPDATE tbl_users SET name=$1, lastname=$2 WHERE id=$3 RETURNING *",
    [data.name, data.lastname, id],
  );

  const user = result.rows[0];

  if (!user) return null;

  // ❗ обновляем кеш
  await redis.setEx(`user:${id}`, 60, JSON.stringify(user));

  // ❗ чистим список
  await redis.del("users:all");

  return user;
};

//
// 🔴 DELETE
//
exports.delete = async (id) => {
  await pg.query("DELETE FROM tbl_users WHERE id=$1", [id]);

  // ❗ удаляем из кеша
  await redis.del(`user:${id}`);
  await redis.del("users:all");
};
