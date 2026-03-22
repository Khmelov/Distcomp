function mapToNewsResponse(news) {
  return {
    id: news.id,
    title: news.title,
    content: news.content,
    creatorId: news.creatorId,
    created: news.created,
    modified: news.modified,
    stickerIds: news.stickerIds || []
  };
}

function mapToNewsEntity(request) {
  return {
    title: request.title,
    content: request.content,
    creatorId: request.creatorId,
    stickerIds: request.stickerIds || []
  };
}

module.exports = { mapToNewsResponse, mapToNewsEntity };