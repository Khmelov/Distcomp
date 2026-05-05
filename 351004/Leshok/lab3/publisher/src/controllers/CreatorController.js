const CreatorService = require('../services/CreatorService');
const { CreatorRequestDto } = require('../dtos/CreatorDto');

class CreatorController {
  async create(req, res, next) {
    try {
      const dto = new CreatorRequestDto(req.body.login, req.body.password, req.body.firstname, req.body.lastname);
      const result = await CreatorService.create(dto);
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async update(req, res, next) {
    try {
      const dto = new CreatorRequestDto(req.body.login, req.body.password, req.body.firstname, req.body.lastname);
      const result = await CreatorService.update(req.params.id, dto);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async getOne(req, res, next) {
    try {
      const result = await CreatorService.getById(req.params.id);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async getAll(req, res, next) {
    try {
      const result = await CreatorService.getAll();
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async delete(req, res, next) {
    try {
      await CreatorService.delete(req.params.id);
      res.status(204).send();
    } catch (err) { next(err); }
  }
}

module.exports = new CreatorController();