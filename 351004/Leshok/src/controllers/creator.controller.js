const creatorService = require('../services/creator.service');
const { AppError } = require('../utils/errors');

class CreatorController {
  async getAll(req, res, next) {
    try {
      const creators = creatorService.getAll();
      res.json(creators);
    } catch (err) {
      next(err);
    }
  }

  async getById(req, res, next) {
    try {
      const { id } = req.params;
      const creator = creatorService.getById(id);
      res.json(creator);
    } catch (err) {
      next(err);
    }
  }

  async create(req, res, next) {
    try {
      const data = req.body;
      const newCreator = creatorService.create(data);
      res.status(201).json(newCreator);
    } catch (err) {
      next(err);
    }
  }

  async update(req, res, next) {
    try {
      const { id } = req.params;
      const data = req.body;
      if (data.id) delete data.id;
      const updated = creatorService.update(id, data);
      res.json(updated);
    } catch (err) {
      next(err);
    }
  }

  async updateWithIdInBody(req, res, next) {
    try {
      const data = req.body;
      if (!data.id) {
        throw new AppError('Missing id in request body', 400, 40001);
      }
      const id = data.id;
      delete data.id;
      const updated = creatorService.update(id, data);
      res.json(updated);
    } catch (err) {
      next(err);
    }
  }

  async delete(req, res, next) {
    try {
      const { id } = req.params;
      creatorService.delete(id);
      res.status(204).send();
    } catch (err) {
      next(err);
    }
  }
}

module.exports = new CreatorController();