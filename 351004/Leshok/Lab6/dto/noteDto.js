// dto/noteDto.js
function toResponse(note) {
  return {
    id: note.id,
    content: note.content,
    newsId: note.newsId,
  };
}

function fromRequest(data) {
  return {
    content: data.content,
    newsId: data.newsId,
  };
}

module.exports = { toResponse, fromRequest };