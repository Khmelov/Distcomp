// controllers/v2/StickerControllerV2.js
const stickerService = require('../../services/StickerService');

exports.getAll = async (req, res, next) => {
  try {
    const stickers = await stickerService.getAll();
    res.json(stickers);
  } catch (err) { next(err); }
};

exports.getById = async (req, res, next) => {
  try {
    const sticker = await stickerService.getById(req.params.id);
    res.json(sticker);
  } catch (err) { next(err); }
};

// Создание стикера доступно только ADMIN (роль проверена в routes)
exports.create = async (req, res, next) => {
  try {
    const sticker = await stickerService.create(req.body);
    res.status(201).json(sticker);
  } catch (err) { next(err); }
};

exports.update = async (req, res, next) => {
  try {
    const updated = await stickerService.update(req.params.id, req.body);
    res.json(updated);
  } catch (err) { next(err); }
};

exports.delete = async (req, res, next) => {
  try {
    await stickerService.delete(req.params.id);
    res.status(204).send();
  } catch (err) { next(err); }
};