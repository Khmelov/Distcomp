// repositories/NoteRepository.js
const BaseRepository = require('./BaseRepository');
const { notes, nextId } = require('../config/db');

class NoteRepository extends BaseRepository {
  constructor() {
    super(notes, () => nextId.note++);
  }

  findByNewsId(newsId) {
    return this.findAll().filter(n => n.newsId === Number(newsId));
  }
}

module.exports = new NoteRepository();