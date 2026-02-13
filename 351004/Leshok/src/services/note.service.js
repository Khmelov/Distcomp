const noteRepo = require('../repositories/note.repository');
const newsRepo = require('../repositories/news.repository');
const { AppError } = require('../utils/errors');
const { mapToNoteResponse, mapToNoteEntity } = require('../dtos/note.dto');

class NoteService {
  getAll() {
    const notes = noteRepo.findAll();
    return notes.map(mapToNoteResponse);
  }

  getById(id) {
    const note = noteRepo.findById(Number(id));
    if (!note) {
      throw new AppError('Note not found', 404, 40440);
    }
    return mapToNoteResponse(note);
  }

  create(data) {
    this.validateNoteData(data);
    const news = newsRepo.findById(Number(data.newsId));
    if (!news) {
      throw new AppError('News not found', 400, 40041);
    }
    const newNote = mapToNoteEntity(data);
    const created = noteRepo.create(newNote);
    return mapToNoteResponse(created);
  }

  update(id, data) {
    const existing = noteRepo.findById(Number(id));
    if (!existing) {
      throw new AppError('Note not found', 404, 40440);
    }
    if (data.newsId) {
      const news = newsRepo.findById(Number(data.newsId));
      if (!news) {
        throw new AppError('News not found', 400, 40041);
      }
    }
    this.validateNoteData(data, true);
    const updatedNote = { ...existing, ...data };
    const result = noteRepo.update(Number(id), updatedNote);
    return mapToNoteResponse(result);
  }

  delete(id) {
    const deleted = noteRepo.delete(Number(id));
    if (!deleted) {
      throw new AppError('Note not found', 404, 40440);
    }
    return true;
  }

  validateNoteData(data, isUpdate = false) {
    const { content } = data;
    if (!isUpdate && !content) {
      throw new AppError('Missing required field: content', 400, 40042);
    }
    if (content && (content.length < 2 || content.length > 2048)) {
      throw new AppError('Content must be between 2 and 2048 characters', 400, 40043);
    }
  }
}

module.exports = new NoteService();