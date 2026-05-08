const express = require("express");
const cache = require("../utils/cache");
const producer = require("../kafka/producer");

const newsRouter = express.Router();

let news = [];

// генерация id
const getNextId = () => {
  if (news.length === 0) return 1;
  return Math.max(...news.map((n) => n.id)) + 1;
};

//
// 🔵 GET ALL (с кешем)
//
newsRouter.get("/news", async (req, res) => {
  const data = await cache.getOrSet("news:all", 60, async () => {
    return news;
  });

  return res.status(200).json(data);
});

//
// 🔵 GET BY ID (с кешем)
//
newsRouter.get("/news/:id", async (req, res) => {
  const id = Number(req.params.id);

  const data = await cache.getOrSet(`news:${id}`, 60, async () => {
    return news.find((n) => n.id === id);
  });

  if (!data) {
    return res.status(404).send({});
  }

  return res.status(200).json(data);
});

//
// 🟡 POST (создание)
//
newsRouter.post("/news", async (req, res) => {
  const newNews = { id: getNextId(), ...req.body };

  news.push(newNews);

  // ❗ инвалидируем список
  await cache.del("news:all");

  return res.status(201).json(newNews);
});

//
// 🟡 PUT (обновление + Kafka)
//
newsRouter.put("/news/:id", async (req, res) => {
  const id = Number(req.params.id);

  const findNew = news.find((n) => n.id === id);

  if (!findNew) {
    return res.status(404).send({});
  }

  const updated = { ...findNew, ...req.body, id };

  news = news.map((n) => (n.id === id ? updated : n));

  // ❗ чистим кеш
  await cache.del(`news:${id}`);
  await cache.del("news:all");

  // 🔥 Kafka событие
  const notice = {
    news_id: id,
    content: `News ${id} updated`,
  };

  await producer.sendNotice(notice);

  return res.status(200).json(updated);
});

//
// 🔴 DELETE
//
newsRouter.delete("/news/:id", async (req, res) => {
  const id = Number(req.params.id);

  const exists = news.find((n) => n.id === id);

  if (!exists) {
    return res.status(404).send({});
  }

  news = news.filter((n) => n.id !== id);

  // ❗ чистим кеш
  await cache.del(`news:${id}`);
  await cache.del("news:all");

  return res.status(204).send();
});

module.exports = newsRouter;
