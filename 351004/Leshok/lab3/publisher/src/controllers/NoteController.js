const NoteService = require('../services/NoteService');

class NoteController {
  async create(req, res, next) {
    try {
      const { newsId, content } = req.body;
      const result = await NoteService.createNote(newsId, content);
      res.status(201).json(result);
    } catch (err) { next(err); }
  }

  async update(req, res, next) {
    try {
      const { id } = req.params;
      const { newsId, content } = req.body;
      const result = await NoteService.updateNote(id, newsId, content);
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
      let result;
      if (newsId) {
        result = await NoteService.getNotesByNewsId(newsId);
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
      await NoteService.deleteNote(id, newsId);
      res.status(204).send();
    } catch (err) { next(err); }
  }
}

module.exports = new NoteController();