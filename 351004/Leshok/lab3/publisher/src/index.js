require('dotenv').config();
const express = require('express');
const { sequelize } = require('./models');
const errorHandler = require('./utils/errorHandler');

const creatorController = require('./controllers/CreatorController');
const newsController = require('./controllers/NewsController');
const stickerController = require('./controllers/StickerController');
const noteController = require('./controllers/NoteController');

const app = express();
app.use(express.json());

// Маршруты для Creator
app.post('/api/v1.0/creators', creatorController.create);
app.get('/api/v1.0/creators', creatorController.getAll);
app.get('/api/v1.0/creators/:id', creatorController.getOne);
app.put('/api/v1.0/creators/:id', creatorController.update);
app.delete('/api/v1.0/creators/:id', creatorController.delete);

// Маршруты для News
app.post('/api/v1.0/news', newsController.create);
app.get('/api/v1.0/news', newsController.getAll);
app.get('/api/v1.0/news/:id', newsController.getOne);
app.put('/api/v1.0/news/:id', newsController.update);
app.delete('/api/v1.0/news/:id', newsController.delete);
app.get('/api/v1.0/news/:id/creator', newsController.getCreatorByNewsId);
app.get('/api/v1.0/news/:id/stickers', newsController.getStickersByNewsId);

// Маршруты для Sticker
app.post('/api/v1.0/stickers', stickerController.create);
app.get('/api/v1.0/stickers', stickerController.getAll);
app.get('/api/v1.0/stickers/:id', stickerController.getOne);
app.put('/api/v1.0/stickers/:id', stickerController.update);
app.delete('/api/v1.0/stickers/:id', stickerController.delete);

// Маршруты для Note (прокси)
// Маршруты для Note (прокси)
app.post('/api/v1.0/notes', noteController.create);
app.put('/api/v1.0/notes/:id', noteController.update);
app.get('/api/v1.0/notes/:id', noteController.getOne);
app.get('/api/v1.0/notes', noteController.getAll);
app.delete('/api/v1.0/notes/:id', noteController.delete);
// Глобальный обработчик ошибок
app.use(errorHandler);

const PORT = process.env.PORT || 24110;
async function start() {
  try {
    await sequelize.sync({ alter: true });
    console.log('PostgreSQL connected & synced');
    app.listen(PORT, () => console.log(`Publisher running on port ${PORT}`));
  } catch (err) {
    console.error('Failed to start publisher:', err);
    process.exit(1);
  }
}
start();