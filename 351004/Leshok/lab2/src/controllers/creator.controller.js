const creatorService = require('../services/creator.service');
const { CreatorRequestTo } = require('../dtos/creator.dto');

class CreatorController {
  async getAll(req, res, next) {
    try {
      const { page, limit, sort, order, ...filters } = req.query;
      const result = await creatorService.getAll(page, limit, sort, order, filters);
      res.json(result);
    } catch (err) { next(err); }
  }

  async getById(req, res, next) {
    try {
      const result = await creatorService.getById(req.params.id);
      res.json(result);
    } catch (err) { next(err); }
  }

  async create(req, res, next) {
    try {
      const dto = new CreatorRequestTo(req.body.login, req.body.password, req.body.firstname, req.body.lastname);
      const result = await creatorService.create(dto);
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async update(req, res, next) {
    try {
      const dto = new CreatorRequestTo(req.body.login, req.body.password, req.body.firstname, req.body.lastname);
      const result = await creatorService.update(req.params.id, dto);
      res.json(result);
    } catch (err) { next(err); }
  }

  async delete(req, res, next) {
    try {
      await creatorService.delete(req.params.id);
      res.status(204).send();
    } catch (err) { next(err); }
  }
}

module.exports = new CreatorController();