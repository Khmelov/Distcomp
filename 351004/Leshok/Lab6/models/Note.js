// models/Note.js
class Note {
  constructor(id, content, newsId) {
    this.id = id;
    this.content = content;
    this.newsId = newsId;
  }
}
module.exports = Note;