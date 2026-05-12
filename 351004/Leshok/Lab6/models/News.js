// models/News.js
class News {
  constructor(id, title, content, creatorId) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.creatorId = creatorId;
    this.created = new Date().toISOString();
    this.modified = new Date().toISOString();
  }
}
module.exports = News;