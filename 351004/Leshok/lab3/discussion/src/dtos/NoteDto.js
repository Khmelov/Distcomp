class NoteRequestDto {
  constructor(newsId, content) {
    this.newsId = newsId;
    this.content = content;
  }
}

class NoteResponseDto {
  constructor(id, newsId, content) {
    this.id = Number(id);
    this.newsId = Number(newsId);
    this.content = content;
  }
}

module.exports = { NoteRequestDto, NoteResponseDto };