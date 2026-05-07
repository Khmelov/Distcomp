function mapToNoteResponse(note) {
  return {
    id: note.id,
    content: note.content,
    newsId: note.newsId
  };
}

function mapToNoteEntity(request) {
  return {
    content: request.content,
    newsId: request.newsId
  };
}

module.exports = { mapToNoteResponse, mapToNoteEntity };