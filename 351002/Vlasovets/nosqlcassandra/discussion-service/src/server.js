const express = require('express');
const axios = require('axios');
const { client, initDb } = require('./db');

const app = express();
app.use(express.json());

let currentId = 1; // Замена автоинкременту для тестов

// Универсальный обработчик ошибок
const sendError = (res, statusCode, errorCode, errorMessage) => {
    res.status(statusCode).json({ errorMessage, errorCode });
};

// Валидатор комментариев
async function validateComment(req, res, next) {
    if (['POST', 'PUT'].includes(req.method)) {
        const body = req.body;
        if (!body || Object.keys(body).length === 0) return sendError(res, 400, 40000, "Empty request body");
        
        if (req.params.id && body.id && String(body.id) !== String(req.params.id)) return sendError(res, 400, 40010, "ID mismatch");
        if (!body.content || !body.storyId) return sendError(res, 400, 40005, "Content and storyId required");
        if (typeof body.content !== 'string' || body.content.length < 2) return sendError(res, 400, 40003, "Content too short");

        // ПРОВЕРКА СУЩЕСТВОВАНИЯ СТАТЬИ ЧЕРЕЗ МИКРОСЕРВИСНЫЙ HTTP-ЗАПРОС В PUBLISHER
        try {
            await axios.get(`http://127.0.0.1:24110/api/v1.0/stories/${body.storyId}`);
        } catch (e) {
            return sendError(res, 400, 40013, "Story not found in Publisher");
        }
    }
    next();
}

const mapRow = row => ({ id: row.id, content: row.content, storyId: row.story_id });

app.get('/api/v1.0/comments', async (req, res) => {
    const result = await client.execute("SELECT * FROM distcomp.tbl_comment",[], { prepare: true });
    res.json(result.rows.map(mapRow));
});

app.get('/api/v1.0/comments/:id', async (req, res) => {
    const result = await client.execute("SELECT * FROM distcomp.tbl_comment WHERE id = ?", [req.params.id], { prepare: true });
    if (result.rowLength === 0) return sendError(res, 404, 40404, "Comment not found");
    res.json(mapRow(result.rows[0]));
});

app.post('/api/v1.0/comments', validateComment, async (req, res) => {
    const id = req.body.id || currentId++;
    await client.execute("INSERT INTO distcomp.tbl_comment (id, story_id, content) VALUES (?, ?, ?)",[id, req.body.storyId, req.body.content], { prepare: true });
    res.status(201).json({ id, storyId: req.body.storyId, content: req.body.content });
});

app.put('/api/v1.0/comments/:id', validateComment, async (req, res) => {
    const id = req.params.id;
    const check = await client.execute("SELECT id FROM distcomp.tbl_comment WHERE id = ?", [id], { prepare: true });
    if (check.rowLength === 0) return sendError(res, 404, 40404, "Comment not found");

    await client.execute("UPDATE distcomp.tbl_comment SET story_id = ?, content = ? WHERE id = ?",[req.body.storyId, req.body.content, id], { prepare: true });
    res.status(200).json({ id: parseInt(id), storyId: req.body.storyId, content: req.body.content });
});

app.delete('/api/v1.0/comments/:id', async (req, res) => {
    const check = await client.execute("SELECT id FROM distcomp.tbl_comment WHERE id = ?", [req.params.id], { prepare: true });
    if (check.rowLength === 0) return sendError(res, 404, 40404, "Comment not found");

    await client.execute("DELETE FROM distcomp.tbl_comment WHERE id = ?", [req.params.id], { prepare: true });
    res.status(204).send();
});

// Служебный роут для Publisher: каскадное удаление комментариев при удалении статьи
app.delete('/api/v1.0/comments/story/:storyId', async (req, res) => {
    // В Cassandra ALLOW FILTERING нужен, так как мы ищем не по PRIMARY KEY (что логично для "ответа" на вопрос про data skew)
    const result = await client.execute("SELECT id FROM distcomp.tbl_comment WHERE story_id = ? ALLOW FILTERING",[req.params.storyId], { prepare: true });
    for (let row of result.rows) {
        await client.execute("DELETE FROM distcomp.tbl_comment WHERE id = ?", [row.id], { prepare: true });
    }
    res.status(204).send();
});

app.use((req, res) => sendError(res, 404, 40400, "Endpoint not found"));

initDb().then(() => app.listen(24130, '0.0.0.0', () => console.log('Discussion Module running on 24130')));