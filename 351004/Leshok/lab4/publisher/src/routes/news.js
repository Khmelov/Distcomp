const express = require('express');
const router = express.Router();
const store = require('../store');

function validateTitle(title) {
    return typeof title === 'string' && title.length >= 2 && title.length <= 64;
}

function validateContent(content) {
    return typeof content === 'string' && content.length >= 4 && content.length <= 2048;
}

router.post('/', (req, res) => {
    const { creatorId, title, content } = req.body;
    if (!creatorId || !title || !content) {
        return res.status(400).json({ errorMessage: 'creatorId, title, content required', errorCode: 40006 });
    }
    if (!validateTitle(title)) {
        return res.status(400).json({ errorMessage: 'Title 2-64 chars', errorCode: 40007 });
    }
    if (!validateContent(content)) {
        return res.status(400).json({ errorMessage: 'Content 4-2048 chars', errorCode: 40008 });
    }
    if (!store.getCreator(creatorId)) {
        return res.status(400).json({ errorMessage: 'Creator not found', errorCode: 40009 });
    }
    const news = store.createNews(req.body);
    res.status(201).json(news);
});

router.get('/', (req, res) => res.json(store.getAllNews()));

router.get('/:id', (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
    const news = store.getNews(id);
    if (!news) return res.status(404).json({ errorMessage: 'News not found', errorCode: 40402 });
    res.json(news);
});

router.put('/:id', (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
    const updated = store.updateNews(id, req.body);
    if (!updated) return res.status(404).json({ errorMessage: 'News not found', errorCode: 40402 });
    res.json(updated);
});

router.delete('/:id', (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
    store.deleteNews(id) ? res.status(204).send() : res.status(404).json({ errorMessage: 'News not found', errorCode: 40402 });
});

module.exports = router;