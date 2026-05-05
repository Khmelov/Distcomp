const noteService = require('../services/note.service');
const { NoteRequestTo } = require('../dtos/note.dto');

class NoteController {
  async getAll(req, res, next) {
    try {
      const { page, limit, sort, order, ...filters } = req.query;
      const result = await noteService.getAll(page, limit, sort, order, filters);
      res.json(result);
    } catch (err) { next(err); }
  }

  async getById(req, res, next) {
    try {
      const result = await noteService.getById(req.params.id);
      res.json(result);
    } catch (err) { next(err); }
  }

  async create(req, res, next) {
    try {
      const dto = new NoteRequestTo(req.body.content, req.body.newsId);
      const result = await noteService.create(dto);
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async update(req, res, next) {
    try {
      const dto = new NoteRequestTo(req.body.content, req.body.newsId);
      const result = await noteService.update(req.params.id, dto);
      res.json(result);
    } catch (err) { next(err); }
  }

  async delete(req, res, next) {
    try {
      await noteService.delete(req.params.id);
      res.status(204).send();
    } catch (err) { next(err); }
  }
}

module.exports = new NoteController();