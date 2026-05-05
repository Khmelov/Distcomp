const express = require('express');
const router = express.Router();
const store = require('../store');

function validateName(name) {
    return typeof name === 'string' && name.length >= 2 && name.length <= 32;
}

router.post('/', (req, res) => {
    const { name } = req.body;
    if (!name) return res.status(400).json({ errorMessage: 'name required', errorCode: 40010 });
    if (!validateName(name)) return res.status(400).json({ errorMessage: 'Name 2-32 chars', errorCode: 40011 });
    const sticker = store.createSticker(req.body);
    res.status(201).json(sticker);
});

router.get('/', (req, res) => res.json(store.getAllStickers()));

router.get('/:id', (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
    const sticker = store.getSticker(id);
    if (!sticker) return res.status(404).json({ errorMessage: 'Sticker not found', errorCode: 40403 });
    res.json(sticker);
});

router.put('/:id', (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
    const updated = store.updateSticker(id, req.body);
    if (!updated) return res.status(404).json({ errorMessage: 'Sticker not found', errorCode: 40403 });
    res.json(updated);
});

router.delete('/:id', (req, res) => {
    const id = parseInt(req.params.id);
    if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id format', errorCode: 40005 });
    store.deleteSticker(id) ? res.status(204).send() : res.status(404).json({ errorMessage: 'Sticker not found', errorCode: 40403 });
});

module.exports = router;