// dto/newsDto.js
function toResponse(news) {
  return {
    id: news.id,
    title: news.title,
    content: news.content,
    created: news.created,
    modified: news.modified,
    creatorId: news.creatorId,
  };
}

function fromRequest(data) {
  return {
    title: data.title,
    content: data.content,
    creatorId: data.creatorId,
  };
}

module.exports = { toResponse, fromRequest };