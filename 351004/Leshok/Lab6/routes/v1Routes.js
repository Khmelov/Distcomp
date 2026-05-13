// routes/v1Routes.js
const express = require('express');
const router = express.Router();
const creatorController = require('../controllers/CreatorController');
const newsController = require('../controllers/NewsController');
const stickerController = require('../controllers/StickerController');
const noteController = require('../controllers/NoteController');

// Creator
router.get('/creators', creatorController.getAll);
router.get('/creators/:id', creatorController.getById);
router.post('/creators', creatorController.create);
router.put('/creators/:id', creatorController.update);
router.delete('/creators/:id', creatorController.delete);

// News
router.get('/news', newsController.getAll);
router.get('/news/:id', newsController.getById);
router.post('/news', newsController.create);
router.put('/news/:id', newsController.update);
router.delete('/news/:id', newsController.delete);

// Stickers
router.get('/stickers', stickerController.getAll);
router.get('/stickers/:id', stickerController.getById);
router.post('/stickers', stickerController.create);
router.put('/stickers/:id', stickerController.update);
router.delete('/stickers/:id', stickerController.delete);

// Notes
router.get('/notes', noteController.getAll);
router.get('/notes/:id', noteController.getById);
router.post('/notes', noteController.create);
router.put('/notes/:id', noteController.update);
router.delete('/notes/:id', noteController.delete);

module.exports = router;