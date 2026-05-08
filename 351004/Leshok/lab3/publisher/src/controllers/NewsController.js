const NewsService = require('../services/NewsService');
const { NewsRequestDto } = require('../dtos/NewsDto');

class NewsController {
  async create(req, res, next) {
    try {
      const dto = new NewsRequestDto(req.body.creatorId, req.body.title, req.body.content);
      const result = await NewsService.create(dto);
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async update(req, res, next) {
    try {
      const dto = new NewsRequestDto(req.body.creatorId, req.body.title, req.body.content);
      const result = await NewsService.update(req.params.id, dto);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async getOne(req, res, next) {
    try {
      const result = await NewsService.getById(req.params.id);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async getAll(req, res, next) {
    try {
      const result = await NewsService.getAll();
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async delete(req, res, next) {
    try {
      await NewsService.delete(req.params.id);
      res.status(204).send();
    } catch (err) { next(err); }
  }

  async getCreatorByNewsId(req, res, next) {
    try {
      const result = await NewsService.getCreatorByNewsId(req.params.id);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async getStickersByNewsId(req, res, next) {
    try {
      const result = await NewsService.getStickersByNewsId(req.params.id);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }
}

module.exports = new NewsController();