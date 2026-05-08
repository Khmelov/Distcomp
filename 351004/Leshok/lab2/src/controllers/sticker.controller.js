const stickerService = require('../services/sticker.service');
const { StickerRequestTo } = require('../dtos/sticker.dto');

class StickerController {
  async getAll(req, res, next) {
    try {
      const { page, limit, sort, order, ...filters } = req.query;
      const result = await stickerService.getAll(page, limit, sort, order, filters);
      res.json(result);
    } catch (err) { next(err); }
  }

  async getById(req, res, next) {
    try {
      const result = await stickerService.getById(req.params.id);
      res.json(result);
    } catch (err) { next(err); }
  }

  async create(req, res, next) {
    try {
      const dto = new StickerRequestTo(req.body.name);
      const result = await stickerService.create(dto);
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async update(req, res, next) {
    try {
      const dto = new StickerRequestTo(req.body.name);
      const result = await stickerService.update(req.params.id, dto);
      res.json(result);
    } catch (err) { next(err); }
  }

  async delete(req, res, next) {
    try {
      await stickerService.delete(req.params.id);
      res.status(204).send();
    } catch (err) { next(err); }
  }
}

module.exports = new StickerController();