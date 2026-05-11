class NoteRequestTo {
  constructor(content, newsId) {
    this.content = content;
    this.newsId = newsId;
  }
}

class NoteResponseTo {
  constructor(id, content, newsId) {
    this.id = Number(id);
    this.content = content;
    this.newsId = Number(newsId);
  }
}

module.exports = { NoteRequestTo, NoteResponseTo };