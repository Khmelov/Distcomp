require('dotenv').config();
const express = require('express');
const cors = require('cors');
const creatorController = require('./controllers/creator.controller');
const newsController = require('./controllers/news.controller');
const stickerController = require('./controllers/sticker.controller');
const noteController = require('./controllers/note.controller');
const errorHandler = require('./middleware/errorHandler');

const app = express();
app.use(cors());
app.use(express.json());

const apiPrefix = '/api/v1.0';

// Маршруты для Creator
app.get(`${apiPrefix}/creators`, creatorController.getAll.bind(creatorController));
app.get(`${apiPrefix}/creators/:id`, creatorController.getById.bind(creatorController));
app.post(`${apiPrefix}/creators`, creatorController.create.bind(creatorController));
app.put(`${apiPrefix}/creators/:id`, creatorController.update.bind(creatorController));
app.delete(`${apiPrefix}/creators/:id`, creatorController.delete.bind(creatorController));

// Маршруты для News
app.get(`${apiPrefix}/news`, newsController.getAll.bind(newsController));
app.get(`${apiPrefix}/news/:id`, newsController.getById.bind(newsController));
app.post(`${apiPrefix}/news`, newsController.create.bind(newsController));
app.put(`${apiPrefix}/news/:id`, newsController.update.bind(newsController));
app.delete(`${apiPrefix}/news/:id`, newsController.delete.bind(newsController));

// Маршруты для Sticker
app.get(`${apiPrefix}/stickers`, stickerController.getAll.bind(stickerController));
app.get(`${apiPrefix}/stickers/:id`, stickerController.getById.bind(stickerController));
app.post(`${apiPrefix}/stickers`, stickerController.create.bind(stickerController));
app.put(`${apiPrefix}/stickers/:id`, stickerController.update.bind(stickerController));
app.delete(`${apiPrefix}/stickers/:id`, stickerController.delete.bind(stickerController));

// Маршруты для Note
app.get(`${apiPrefix}/notes`, noteController.getAll.bind(noteController));
app.get(`${apiPrefix}/notes/:id`, noteController.getById.bind(noteController));
app.post(`${apiPrefix}/notes`, noteController.create.bind(noteController));
app.put(`${apiPrefix}/notes/:id`, noteController.update.bind(noteController));
app.delete(`${apiPrefix}/notes/:id`, noteController.delete.bind(noteController));

app.use(errorHandler);

module.exports = app;