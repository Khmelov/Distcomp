const express = require('express');
const router = express.Router();
const { getNote, createNote, updateNote, deleteNote, getAllNotes } = require('../models/note');
const { moderate } = require('../moderation');

router.get('/', async (req, res) => {
    try {
        const notes = await getAllNotes();
        res.json(notes);
    } catch (e) {
        console.error(e);
        res.status(500).json({ errorMessage: e.message, errorCode: 50030 });
    }
});

router.post('/', async (req, res) => {
    try {
        const { content, newsId } = req.body;
        if (!content || newsId === undefined) {
            return res.status(400).json({ errorMessage: 'content and newsId required', errorCode: 40030 });
        }
        const newsIdInt = parseInt(newsId, 10);
        if (isNaN(newsIdInt)) {
            return res.status(400).json({ errorMessage: 'newsId must be an integer', errorCode: 40031 });
        }
        const note = await createNote({ content, newsId: newsIdInt });
        note.state = moderate(content);
        await updateNote(note.id, { state: note.state });
        res.status(201).json(note);
    } catch (e) {
        console.error('POST error:', e);
        res.status(500).json({ errorMessage: e.message, errorCode: 50030 });
    }
});

router.get('/:id', async (req, res) => {
    try {
        const id = parseInt(req.params.id);
        if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id', errorCode: 40032 });
        const note = await getNote(id);
        if (!note) return res.status(404).json({ errorMessage: 'Note not found', errorCode: 40430 });
        res.json(note);
    } catch (e) {
        console.error(e);
        res.status(404).json({ errorMessage: 'Note not found', errorCode: 40430 });
    }
});

router.put('/:id', async (req, res) => {
    try {
        const id = parseInt(req.params.id);
        if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id', errorCode: 40032 });
        const note = await getNote(id);
        if (!note) return res.status(404).json({ errorMessage: 'Note not found', errorCode: 40430 });

        const updates = {};
        if (req.body.content !== undefined) updates.content = req.body.content;
        if (req.body.newsId !== undefined) {
            const newsIdInt = parseInt(req.body.newsId, 10);
            if (isNaN(newsIdInt)) return res.status(400).json({ errorMessage: 'newsId must be integer', errorCode: 40031 });
            updates.newsId = newsIdInt;
        }
        await updateNote(id, updates);
        if (updates.content) {
            const state = moderate(updates.content);
            await updateNote(id, { state });
        }
        const updated = await getNote(id);
        res.json(updated);
    } catch (e) {
        console.error(e);
        res.status(404).json({ errorMessage: 'Note not found', errorCode: 40430 });
    }
});

router.delete('/:id', async (req, res) => {
    try {
        const id = parseInt(req.params.id);
        if (isNaN(id)) return res.status(400).json({ errorMessage: 'Invalid id', errorCode: 40032 });
        const note = await getNote(id);
        if (!note) return res.status(404).json({ errorMessage: 'Note not found', errorCode: 40430 });
        await deleteNote(id);
        res.status(204).send();
    } catch (e) {
        console.error(e);
        res.status(404).json({ errorMessage: 'Note not found', errorCode: 40430 });
    }
});

module.exports = router;