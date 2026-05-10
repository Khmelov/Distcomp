class NewsRequestDto {
  constructor(creatorId, title, content) {
    this.creatorId = creatorId;
    this.title = title;
    this.content = content;
  }
}

class NewsResponseDto {
  constructor(id, creatorId, title, content, created, modified) {
    this.id = Number(id);
    this.creatorId = Number(creatorId);
    this.title = title;
    this.content = content;
    this.created = created;
    this.modified = modified;
  }
}

module.exports = { NewsRequestDto, NewsResponseDto };