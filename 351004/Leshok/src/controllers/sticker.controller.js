const stickerService = require('../services/sticker.service');

class StickerController {
  async getAll(req, res, next) {
    try {
      const stickers = stickerService.getAll();
      res.json(stickers);
    } catch (err) {
      next(err);
    }
  }

  async getById(req, res, next) {
    try {
      const { id } = req.params;
      const sticker = stickerService.getById(id);
      res.json(sticker);
    } catch (err) {
      next(err);
    }
  }

  async create(req, res, next) {
    try {
      const data = req.body;
      const newSticker = stickerService.create(data);
      res.status(201).json(newSticker);
    } catch (err) {
      next(err);
    }
  }

  async update(req, res, next) {
    try {
      const { id } = req.params;
      const data = req.body;
      const updated = stickerService.update(id, data);
      res.json(updated);
    } catch (err) {
      next(err);
    }
  }

  async delete(req, res, next) {
    try {
      const { id } = req.params;
      stickerService.delete(id);
      res.status(204).send();
    } catch (err) {
      next(err);
    }
  }
}

module.exports = new StickerController();