const express = require('express');
const router = express.Router();

const creatorController = require('../controllers/creator.controller');
const newsController = require('../controllers/news.controller');
const stickerController = require('../controllers/sticker.controller');
const noteController = require('../controllers/note.controller');

const {
  validateCreator,
  validateNews,
  validateSticker,
  validateNote,
  validateId
} = require('../middlewares/validation');

router.get('/creators', creatorController.getAll);
router.get('/creators/:id', validateId, creatorController.getById);
router.post('/creators', validateCreator, creatorController.create);
router.put('/creators/:id', validateId, validateCreator, creatorController.update);
router.put('/creators', validateCreator, creatorController.updateWithIdInBody); // новый маршрут
router.delete('/creators/:id', validateId, creatorController.delete);
router.get('/creators/by-news/:newsId', validateId, newsController.getCreatorByNewsId);

router.get('/news', newsController.getAll);
router.get('/news/:id', validateId, newsController.getById);
router.post('/news', validateNews, newsController.create);
router.put('/news/:id', validateId, validateNews, newsController.update);
router.put('/news', validateNews, newsController.updateWithIdInBody); // новый маршрут
router.delete('/news/:id', validateId, newsController.delete);
router.get('/news/:newsId/creator', validateId, newsController.getCreatorByNewsId);
router.get('/news/:newsId/stickers', validateId, newsController.getStickersByNewsId);
router.get('/news/:newsId/notes', validateId, newsController.getNotesByNewsId);
router.get('/news/search', newsController.searchNews);

router.get('/stickers', stickerController.getAll);
router.get('/stickers/:id', validateId, stickerController.getById);
router.post('/stickers', validateSticker, stickerController.create);
router.put('/stickers/:id', validateId, validateSticker, stickerController.update);
router.delete('/stickers/:id', validateId, stickerController.delete);

router.get('/notes', noteController.getAll);
router.get('/notes/:id', validateId, noteController.getById);
router.post('/notes', validateNote, noteController.create);
router.put('/notes/:id', validateId, validateNote, noteController.update);
router.delete('/notes/:id', validateId, noteController.delete);

module.exports = router;