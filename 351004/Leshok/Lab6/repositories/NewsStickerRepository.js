// repositories/NewsStickerRepository.js
const { newsStickers } = require('../config/db');

class NewsStickerRepository {
  addStickerToNews(newsId, stickerId) {
    newsId = Number(newsId);
    stickerId = Number(stickerId);
    if (!newsStickers.has(newsId)) {
      newsStickers.set(newsId, new Set());
    }
    newsStickers.get(newsId).add(stickerId);
  }

  removeStickerFromNews(newsId, stickerId) {
    newsId = Number(newsId);
    const stickers = newsStickers.get(newsId);
    if (stickers) {
      stickers.delete(Number(stickerId));
      if (stickers.size === 0) newsStickers.delete(newsId);
    }
  }

  getStickersByNewsId(newsId) {
    const stickerIds = newsStickers.get(Number(newsId)) || new Set();
    return Array.from(stickerIds);
  }

  getNewsIdsByStickerId(stickerId) {
    const result = [];
    for (let [newsId, stickers] of newsStickers.entries()) {
      if (stickers.has(Number(stickerId))) result.push(newsId);
    }
    return result;
  }

  deleteAllByNewsId(newsId) {
    newsStickers.delete(Number(newsId));
  }
}

module.exports = new NewsStickerRepository();