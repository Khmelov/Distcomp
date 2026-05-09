class NewsRequestTo {
  constructor(title, content, creatorId, stickerIds = []) {
    this.title = title;
    this.content = content;
    this.creatorId = creatorId;
    this.stickerIds = stickerIds; 
  }
}

class NewsResponseTo {
  constructor(id, title, content, created, modified, creatorId) {
    this.id = Number(id);
    this.title = title;
    this.content = content;
    this.created = created;
    this.modified = modified;
    this.creatorId = Number(creatorId);
  }
}

module.exports = { NewsRequestTo, NewsResponseTo };