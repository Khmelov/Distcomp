const express = require("express");
const cache = require("../utils/cache");
const producer = require("../kafka/producer");

const noticesRouter = express.Router();

let notices = [];

// id генерация
const getNextId = () => {
  if (notices.length === 0) return 1;
  return Math.max(...notices.map((n) => n.id)) + 1;
};

//
// 🔵 GET ALL (с кешем)
//
noticesRouter.get("/notices", async (req, res) => {
  const data = await cache.getOrSet("notices:all", 60, async () => {
    return notices;
  });

  return res.status(200).json(data);
});

//
// 🔵 GET BY ID (с кешем)
//
noticesRouter.get("/notices/:id", async (req, res) => {
  const id = Number(req.params.id);

  const data = await cache.getOrSet(`notice:${id}`, 60, async () => {
    return notices.find((n) => n.id === id);
  });

  if (!data) {
    return res.status(404).send({});
  }

  return res.status(200).json(data);
});

//
// 🟡 POST (создание + Kafka)
//
noticesRouter.post("/notices", async (req, res) => {
  const newNotice = {
    id: getNextId(),
    ...req.body,
  };

  notices.push(newNotice);

  // ❗ чистим кеш
  await cache.del("notices:all");

  // 🔥 отправка в Kafka
  await producer.sendNotice({
    news_id: newNotice.news_id,
    content: newNotice.content,
  });

  return res.status(201).json(newNotice);
});

//
// 🟡 PUT (обновление)
//
noticesRouter.put("/notices/:id", async (req, res) => {
  const id = Number(req.params.id);

  const findNotice = notices.find((n) => n.id === id);

  if (!findNotice) {
    return res.status(404).send({});
  }

  const updated = { ...findNotice, ...req.body, id };

  notices = notices.map((n) => (n.id === id ? updated : n));

  // ❗ чистим кеш
  await cache.del(`notice:${id}`);
  await cache.del("notices:all");

  return res.status(200).json(updated);
});

//
// 🔴 DELETE
//
noticesRouter.delete("/notices/:id", async (req, res) => {
  const id = Number(req.params.id);

  const exists = notices.find((n) => n.id === id);

  if (!exists) {
    return res.status(404).send({});
  }

  notices = notices.filter((n) => n.id !== id);

  // ❗ чистим кеш
  await cache.del(`notice:${id}`);
  await cache.del("notices:all");

  return res.status(204).send();
});

module.exports = noticesRouter;
