// repositories/StickerRepository.js
const BaseRepository = require('./BaseRepository');
const { stickers, nextId } = require('../config/db');

class StickerRepository extends BaseRepository {
  constructor() {
    super(stickers, () => nextId.sticker++);
  }
}

module.exports = new StickerRepository();