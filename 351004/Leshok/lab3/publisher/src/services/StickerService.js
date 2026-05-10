const { Sticker, News } = require('../models');
const { StickerResponseDto } = require('../dtos/StickerDto');

class StickerService {
  async create(dto) {
    const { name } = dto;
    const sticker = await Sticker.create({ name });
    return new StickerResponseDto(sticker.id, sticker.name);
  }

  async update(id, dto) {
    const sticker = await Sticker.findByPk(id);
    if (!sticker) throw { status: 404, code: '40403', message: 'Sticker not found' };
    const { name } = dto;
    await sticker.update({ name });
    return new StickerResponseDto(sticker.id, sticker.name);
  }

  async getById(id) {
    const sticker = await Sticker.findByPk(id);
    if (!sticker) throw { status: 404, code: '40403', message: 'Sticker not found' };
    return new StickerResponseDto(sticker.id, sticker.name);
  }

  async getAll() {
    const stickers = await Sticker.findAll();
    return stickers.map(s => new StickerResponseDto(s.id, s.name));
  }

  async delete(id) {
    const sticker = await Sticker.findByPk(id);
    if (!sticker) throw { status: 404, code: '40403', message: 'Sticker not found' };
    await sticker.destroy();
  }
}

module.exports = new StickerService();