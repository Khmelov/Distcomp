const newsService = require('../services/news.service');
const { NewsRequestTo } = require('../dtos/news.dto');

class NewsController {
  async getAll(req, res, next) {
    try {
      const { page, limit, sort, order, ...filters } = req.query;
      const result = await newsService.getAll(page, limit, sort, order, filters);
      res.json(result);
    } catch (err) { next(err); }
  }

  async getById(req, res, next) {
    try {
      const result = await newsService.getById(req.params.id);
      res.json(result);
    } catch (err) { next(err); }
  }

  async create(req, res, next) {
    try {
      const dto = new NewsRequestTo(req.body.title, req.body.content, req.body.creatorId, req.body.stickerIds);
      const result = await newsService.create(dto);
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async update(req, res, next) {
    try {
      const dto = new NewsRequestTo(req.body.title, req.body.content, req.body.creatorId, req.body.stickerIds);
      const result = await newsService.update(req.params.id, dto);
      res.json(result);
    } catch (err) { next(err); }
  }

  async delete(req, res, next) {
    try {
      await newsService.delete(req.params.id);
      res.status(204).send();
    } catch (err) { next(err); }
  }
}

module.exports = new NewsController();