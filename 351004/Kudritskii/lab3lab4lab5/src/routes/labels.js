const express = require("express");
const cache = require("../utils/cache");

const labelsRouter = express.Router();

let labels = [];

// генерация id
const getNextId = () => {
  if (labels.length === 0) return 1;
  return Math.max(...labels.map((l) => l.id)) + 1;
};

//
// 🔵 GET ALL (с кешем)
//
labelsRouter.get("/labels", async (req, res) => {
  const data = await cache.getOrSet("labels:all", 60, async () => {
    return labels;
  });

  return res.status(200).json(data);
});

//
// 🔵 GET BY ID (с кешем)
//
labelsRouter.get("/labels/:id", async (req, res) => {
  const id = Number(req.params.id);

  const data = await cache.getOrSet(`label:${id}`, 60, async () => {
    return labels.find((l) => l.id === id);
  });

  if (!data) {
    return res.status(404).send({});
  }

  return res.status(200).json(data);
});

//
// 🟡 CREATE
//
labelsRouter.post("/labels", async (req, res) => {
  const newLabel = {
    id: getNextId(),
    ...req.body,
  };

  labels.push(newLabel);

  // ❗ чистим кеш
  await cache.del("labels:all");

  return res.status(201).json(newLabel);
});

//
// 🟡 UPDATE
//
labelsRouter.put("/labels/:id", async (req, res) => {
  const id = Number(req.params.id);

  const findLabel = labels.find((l) => l.id === id);

  if (!findLabel) {
    return res.status(404).send({});
  }

  const updated = { ...findLabel, ...req.body, id };

  labels = labels.map((l) => (l.id === id ? updated : l));

  // ❗ чистим кеш
  await cache.del(`label:${id}`);
  await cache.del("labels:all");

  return res.status(200).json(updated);
});

//
// 🔴 DELETE
//
labelsRouter.delete("/labels/:id", async (req, res) => {
  const id = Number(req.params.id);

  const exists = labels.find((l) => l.id === id);

  if (!exists) {
    return res.status(404).send({});
  }

  labels = labels.filter((l) => l.id !== id);

  // ❗ чистим кеш
  await cache.del(`label:${id}`);
  await cache.del("labels:all");

  return res.status(204).send();
});

module.exports = labelsRouter;
