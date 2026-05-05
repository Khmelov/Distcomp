const NoteRepository = require('../repositories/NoteRepository');
const { generateId } = require('../utils/idGenerator');
const { NoteResponseDto } = require('../dtos/NoteDto');

class NoteService {
  async createNote(country, newsId, content) {
    this.validateContent(content);
    const id = generateId();
    const note = { id, newsId, country, content };
    await NoteRepository.save(note);
    return new NoteResponseDto(Number(id), Number(newsId), content);
  }

  async updateNote(id, content, newsId = null, country = 'default') {
    this.validateContent(content);
    if (newsId) {
      const existing = await NoteRepository.findByIdOnly(id);
      if (!existing) throw { status: 404, code: '40401', message: 'Note not found' };
      if (existing.newsId !== Number(newsId)) {
        throw { status: 400, code: '40003', message: 'newsId mismatch' };
      }
      await NoteRepository.updateFull({ id, newsId, country, content });
    } else {
      const updated = await NoteRepository.updateByIdOnly(id, content);
      if (!updated) throw { status: 404, code: '40401', message: 'Note not found' };
      newsId = updated.newsId;
    }
    return new NoteResponseDto(Number(id), Number(newsId), content);
  }

  async getNote(id) {
    const note = await NoteRepository.findByIdOnly(id);
    if (!note) throw { status: 404, code: '40401', message: 'Note not found' };
    return new NoteResponseDto(note.id, note.newsId, note.content);
  }

  async getAllNotes() {
    const notes = await NoteRepository.findAll();
    return notes.map(n => new NoteResponseDto(n.id, n.newsId, n.content));
  }

  async getNotesByNewsId(country, newsId) {
    const notes = await NoteRepository.findByNewsId(country, newsId);
    return notes.map(n => new NoteResponseDto(n.id, n.newsId, n.content));
  }

  async deleteNote(id, newsId = null, country = 'default') {
    if (newsId) {
      const note = await NoteRepository.findByIdOnly(id);
      if (!note || note.newsId !== Number(newsId)) {
        throw { status: 404, code: '40401', message: 'Note not found' };
      }
      await NoteRepository.updateFull({ id, newsId, country, content: note.content }); // не нужно, просто для удаления
      // Реальное удаление:
      const query = `DELETE FROM tbl_note WHERE country = ? AND newsId = ? AND id = ?`;
      const { client } = require('../config/cassandra');
      await client.execute(query, [country, Number(newsId), Number(id)], { prepare: true });
    } else {
      const success = await NoteRepository.deleteByIdOnly(id);
      if (!success) throw { status: 404, code: '40401', message: 'Note not found' };
    }
  }

  validateContent(content) {
    if (!content || content.length < 2 || content.length > 2048) {
      const err = new Error('Content length must be between 2 and 2048');
      err.status = 400;
      err.code = '40001';
      throw err;
    }
  }
}

module.exports = new NoteService();