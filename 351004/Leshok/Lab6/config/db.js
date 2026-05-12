// stores: creators, news, stickers, notes, and many-to-many mapping (news <-> stickers)
const creators = new Map();      // id -> creator object
const news = new Map();          // id -> news object
const stickers = new Map();      // id -> sticker object
const notes = new Map();         // id -> note object
const newsStickers = new Map();  // newsId -> Set(stickerIds)

let nextId = {
  creator: 1,
  news: 1,
  sticker: 1,
  note: 1,
};

module.exports = {
  creators,
  news,
  stickers,
  notes,
  newsStickers,
  nextId,
};