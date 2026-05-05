const StickerService = require('../services/StickerService');
const { StickerRequestDto } = require('../dtos/StickerDto');

class StickerController {
  async create(req, res, next) {
    try {
      const dto = new StickerRequestDto(req.body.name);
      const result = await StickerService.create(dto);
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async update(req, res, next) {
    try {
      const dto = new StickerRequestDto(req.body.name);
      const result = await StickerService.update(req.params.id, dto);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async getOne(req, res, next) {
    try {
      const result = await StickerService.getById(req.params.id);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async getAll(req, res, next) {
    try {
      const result = await StickerService.getAll();
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async delete(req, res, next) {
    try {
      await StickerService.delete(req.params.id);
      res.status(204).send();
    } catch (err) { next(err); }
  }
}

module.exports = new StickerController();