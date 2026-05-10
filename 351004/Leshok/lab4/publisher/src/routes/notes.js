const express = require('express');
const router = express.Router();
const axios = require('axios');
const { sendRequest } = require('../kafka/producer');
const { waitForResponse } = require('../kafka/consumer');
const store = require('../store');

const DISCUSSION_URL = 'http://localhost:24130/api/v1.0/notes';

router.get('/', async (req, res) => {
    try {
        const response = await axios.get(DISCUSSION_URL);
        res.json(response.data);
    } catch (e) {
        if (e.response) {
            res.status(e.response.status).json(e.response.data);
        } else {
            res.status(500).json({ errorMessage: e.message, errorCode: 50001 });
        }
    }
});

router.get('/:id', async (req, res) => {
    try {
        const response = await axios.get(`${DISCUSSION_URL}/${req.params.id}`);
        res.json(response.data);
    } catch (e) {
        if (e.response) {
            res.status(e.response.status).json(e.response.data);
        } else {
            res.status(500).json({ errorMessage: e.message, errorCode: 50001 });
        }
    }
});

router.post('/', async (req, res) => {
    try {
        const { content, newsId } = req.body;
        if (!content || newsId === undefined) {
            return res.status(400).json({ errorMessage: 'content and newsId required', errorCode: 40012 });
        }
        if (typeof newsId !== 'number' || !store.getNews(newsId)) {
            return res.status(400).json({ errorMessage: 'newsId not found', errorCode: 40014 });
        }
        const cid = await sendRequest('CREATE', { content, newsId }, newsId);
        const resp = await waitForResponse(cid);
        if (resp.success) res.status(201).json(resp.note);
        else res.status(400).json({ errorMessage: resp.error, errorCode: 40015 });
    } catch (e) {
        res.status(500).json({ errorMessage: e.message, errorCode: 50001 });
    }
});

router.put('/:id', async (req, res) => {
    try {
        const { newsId, content } = req.body;
        if (newsId !== undefined && (typeof newsId !== 'number' || !store.getNews(newsId))) {
            return res.status(400).json({ errorMessage: 'newsId not found', errorCode: 40014 });
        }
        const cid = await sendRequest('UPDATE', { id: req.params.id, ...req.body }, newsId || '0');
        const resp = await waitForResponse(cid);
        if (resp.success) res.json(resp.note);
        else res.status(404).json({ errorMessage: resp.error, errorCode: 40404 });  // <-- 404
    } catch (e) {
        res.status(500).json({ errorMessage: e.message, errorCode: 50001 });
    }
});

router.delete('/:id', async (req, res) => {
    try {
        const cid = await sendRequest('DELETE', { id: req.params.id }, '0');
        const resp = await waitForResponse(cid);
        if (resp.success) res.status(204).send();
        else res.status(404).json({ errorMessage: resp.error, errorCode: 40404 });   // <-- 404
    } catch (e) {
        res.status(500).json({ errorMessage: e.message, errorCode: 50001 });
    }
});

module.exports = router;