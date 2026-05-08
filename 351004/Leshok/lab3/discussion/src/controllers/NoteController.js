const NoteService = require('../services/NoteService');

class NoteController {
  async create(req, res, next) {
    try {
      const { newsId, content } = req.body;
      const country = req.headers['x-country'] || 'default';
      const result = await NoteService.createNote(country, newsId, content);
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async update(req, res, next) {
    try {
      const { id } = req.params;
      const { newsId, content } = req.body;
      const country = req.headers['x-country'] || 'default';
      const result = await NoteService.updateNote(id, content, newsId, country);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async getOne(req, res, next) {
    try {
      const { id } = req.params;
      const result = await NoteService.getNote(id);
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async getAll(req, res, next) {
    try {
      const { newsId } = req.query;
      const country = req.headers['x-country'] || 'default';
      let result;
      if (newsId) {
        result = await NoteService.getNotesByNewsId(country, newsId);
      } else {
        result = await NoteService.getAllNotes();
      }
      res.status(200).json(result);
    } catch (err) { next(err); }
  }

  async delete(req, res, next) {
    try {
      const { id } = req.params;
      const { newsId } = req.query;
      const country = req.headers['x-country'] || 'default';
      await NoteService.deleteNote(id, newsId, country);
      res.status(204).send();
    } catch (err) { next(err); }
  }
}

module.exports = new NoteController();