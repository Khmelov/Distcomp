const noteService = require('../services/note.service');

class NoteController {
  async getAll(req, res, next) {
    try {
      const notes = noteService.getAll();
      res.json(notes);
    } catch (err) {
      next(err);
    }
  }

  async getById(req, res, next) {
    try {
      const { id } = req.params;
      const note = noteService.getById(id);
      res.json(note);
    } catch (err) {
      next(err);
    }
  }

  async create(req, res, next) {
    try {
      const data = req.body;
      const newNote = noteService.create(data);
      res.status(201).json(newNote);
    } catch (err) {
      next(err);
    }
  }

  async update(req, res, next) {
    try {
      const { id } = req.params;
      const data = req.body;
      const updated = noteService.update(id, data);
      res.json(updated);
    } catch (err) {
      next(err);
    }
  }

  async delete(req, res, next) {
    try {
      const { id } = req.params;
      noteService.delete(id);
      res.status(204).send();
    } catch (err) {
      next(err);
    }
  }
}

module.exports = new NoteController();