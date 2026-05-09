const BaseRepository = require('../repositories/base.repository');
const { Note } = require('../models');
const { NoteRequestTo, NoteResponseTo } = require('../dtos/note.dto');

class NoteService {
  constructor() {
    this.repository = new BaseRepository(Note);
  }

  async getAll(page, limit, sort, order, filters) {
    if (page === undefined && limit === undefined) {
      const all = await Note.findAll({
        where: filters,
        order: [[sort || 'id', (order || 'ASC').toUpperCase()]]
      });
      return all.map(n => new NoteResponseTo(n.id, n.content, n.newsId));
    }

    const result = await this.repository.findAll({ page, limit, sort, order, filters });
    return {
      data: result.data.map(n => new NoteResponseTo(n.id, n.content, n.newsId)),
      total: result.total,
      page: result.page,
      totalPages: result.totalPages
    };
  }

  async getById(id) {
    const note = await this.repository.findById(id);
    return new NoteResponseTo(note.id, note.content, note.newsId);
  }

  async create(noteRequest) {
    const note = await this.repository.create({
      content: noteRequest.content,
      newsId: noteRequest.newsId
    });
    return new NoteResponseTo(note.id, note.content, note.newsId);
  }

  async update(id, noteRequest) {
    const updated = await this.repository.update(id, {
      content: noteRequest.content,
      newsId: noteRequest.newsId
    });
    return new NoteResponseTo(updated.id, updated.content, updated.newsId);
  }

  async delete(id) {
    await this.repository.delete(id);
  }
}

module.exports = new NoteService();