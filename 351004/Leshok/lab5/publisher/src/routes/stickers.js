const express = require('express');
const router = express.Router();
const store = require('../store');
const cache = require('../redisClient');

function validateName(name) {
    return typeof name === 'string' && name.length >= 2 && name.length <= 32;
}

router.post('/', async (req, res) => {
    const { name } = req.body;
    if (!name) {
        return res.status(400).json({ errorMessage: 'name required', errorCode: 40010 });
    }
    if (!validateName(name)) {
        return res.status(400).json({ errorMessage: 'Name 2-32 chars', errorCode: 40011 });
    }

    const sticker = await store.createSticker(req.body);
    await cache.del('stickers:all');
    res.status(201).json(sticker);
});

router.get('/', async (req, res) => {
    const cached = await cache.get('stickers:all');
    if (cached) {
        return res.json(cached);
    }

    const data = await store.getAllStickers();
    await cache.set('stickers:all', data);
    res.json(data);
});

router.get('/:id', async (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) {
        return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
    }

    const key = `sticker:${id}`;
    const cached = await cache.get(key);
    if (cached) {
        return res.json(cached);
    }

    const sticker = await store.getSticker(id);
    if (!sticker) {
        return res.status(404).json({ errorMessage: 'Sticker not found', errorCode: 40403 });
    }

    await cache.set(key, sticker);
    res.json(sticker);
});

router.put('/:id', async (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) {
        return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
    }

    const updated = await store.updateSticker(id, req.body);
    if (!updated) {
        return res.status(404).json({ errorMessage: 'Sticker not found', errorCode: 40403 });
    }

    await cache.del(`sticker:${id}`);
    await cache.del('stickers:all');
    res.json(updated);
});

router.delete('/:id', async (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) {
        return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
    }

    const deleted = await store.deleteSticker(id);
    if (!deleted) {
        return res.status(404).json({ errorMessage: 'Sticker not found', errorCode: 40403 });
    }

    await cache.del(`sticker:${id}`);
    await cache.del('stickers:all');
    res.status(204).send();
});

module.exports = router;