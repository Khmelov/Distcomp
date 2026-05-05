const express = require('express');
const router = express.Router();
const store = require('../store');
const cache = require('../redisClient');

function validateTitle(title) { return typeof title === 'string' && title.length >= 2 && title.length <= 64; }
function validateContent(content) { return typeof content === 'string' && content.length >= 4 && content.length <= 2048; }

router.post('/', async (req, res) => {
  const { creatorId, title, content } = req.body;
  if (!creatorId || !title || !content)
    return res.status(400).json({ errorMessage: 'creatorId, title, content required', errorCode: 40006 });
  if (!validateTitle(title)) return res.status(400).json({ errorMessage: 'Title 2-64 chars', errorCode: 40007 });
  if (!validateContent(content)) return res.status(400).json({ errorMessage: 'Content 4-2048 chars', errorCode: 40008 });
  if (!(await store.getCreator(creatorId)))
    return res.status(400).json({ errorMessage: 'Creator not found', errorCode: 40009 });
  const news = await store.createNews(req.body);
  await cache.del('news:all');
  res.status(201).json(news);
});

router.get('/', async (req, res) => {
  const cached = await cache.get('news:all');
  if (cached) return res.json(cached);
  const data = await store.getAllNews();
  await cache.set('news:all', data);
  res.json(data);
});

router.get('/:id', async (req, res) => {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
  const key = `news:${id}`;
  const cached = await cache.get(key);
  if (cached) return res.json(cached);
  const news = await store.getNews(id);
  if (!news) return res.status(404).json({ errorMessage: 'News not found', errorCode: 40402 });
  await cache.set(key, news);
  res.json(news);
});

router.put('/:id', async (req, res) => {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
  const updated = await store.updateNews(id, req.body);
  if (!updated) return res.status(404).json({ errorMessage: 'News not found', errorCode: 40402 });
  await cache.del(`news:${id}`);
  await cache.del('news:all');
  res.json(updated);
});

router.delete('/:id', async (req, res) => {
  const id = parseInt(req.params.id);
  if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
  const deleted = await store.deleteNews(id);
  if (!deleted) return res.status(404).json({ errorMessage: 'News not found', errorCode: 40402 });
  await cache.del(`news:${id}`);
  await cache.del('news:all');
  res.status(204).send();
});

module.exports = router;