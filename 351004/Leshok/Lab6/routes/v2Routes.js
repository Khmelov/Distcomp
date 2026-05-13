// routes/v2Routes.js
const express = require('express');
const router = express.Router();
const { verifyToken, checkRole } = require('../middleware/auth');
const authController = require('../controllers/AuthController');

// Импортируем v2-контроллеры (с передачей пользователя)
const creatorControllerV2 = require('../controllers/v2/CreatorControllerV2');
const newsControllerV2 = require('../controllers/v2/NewsControllerV2');
const stickerControllerV2 = require('../controllers/v2/StickerControllerV2');
const noteControllerV2 = require('../controllers/v2/NoteControllerV2');

// Публичные эндпоинты
router.post('/creators', creatorControllerV2.create); // регистрация
router.post('/login', authController.login);

// Все остальные требуют аутентификации
router.use(verifyToken);

// Creator
router.get('/creators', checkRole('ADMIN'), creatorControllerV2.getAll);
router.get('/creators/:id', checkRole('ADMIN', 'CUSTOMER'), creatorControllerV2.getById);
router.put('/creators/:id', checkRole('ADMIN'), creatorControllerV2.update);
router.delete('/creators/:id', checkRole('ADMIN'), creatorControllerV2.delete);

// News
router.get('/news', checkRole('ADMIN', 'CUSTOMER'), newsControllerV2.getAll);
router.get('/news/:id', checkRole('ADMIN', 'CUSTOMER'), newsControllerV2.getById);
router.post('/news', checkRole('ADMIN', 'CUSTOMER'), newsControllerV2.create);
router.put('/news/:id', checkRole('ADMIN', 'CUSTOMER'), newsControllerV2.update);
router.delete('/news/:id', checkRole('ADMIN', 'CUSTOMER'), newsControllerV2.delete);

// Stickers (доступно всем аутентифицированным, но модификация только ADMIN)
router.get('/stickers', checkRole('ADMIN', 'CUSTOMER'), stickerControllerV2.getAll);
router.get('/stickers/:id', checkRole('ADMIN', 'CUSTOMER'), stickerControllerV2.getById);
router.post('/stickers', checkRole('ADMIN'), stickerControllerV2.create);
router.put('/stickers/:id', checkRole('ADMIN'), stickerControllerV2.update);
router.delete('/stickers/:id', checkRole('ADMIN'), stickerControllerV2.delete);

// Notes (аналогично)
router.get('/notes', checkRole('ADMIN', 'CUSTOMER'), noteControllerV2.getAll);
router.get('/notes/:id', checkRole('ADMIN', 'CUSTOMER'), noteControllerV2.getById);
router.post('/notes', checkRole('ADMIN', 'CUSTOMER'), noteControllerV2.create);
router.put('/notes/:id', checkRole('ADMIN', 'CUSTOMER'), noteControllerV2.update);
router.delete('/notes/:id', checkRole('ADMIN', 'CUSTOMER'), noteControllerV2.delete);

module.exports = router;