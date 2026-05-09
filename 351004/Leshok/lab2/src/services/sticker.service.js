const BaseRepository = require('../repositories/base.repository');
const { Sticker } = require('../models');
const { StickerRequestTo, StickerResponseTo } = require('../dtos/sticker.dto');

class StickerService {
  constructor() {
    this.repository = new BaseRepository(Sticker);
  }

  async getAll(page, limit, sort, order, filters) {
    if (page === undefined && limit === undefined) {
      const all = await Sticker.findAll({
        where: filters,
        order: [[sort || 'id', (order || 'ASC').toUpperCase()]]
      });
      return all.map(s => new StickerResponseTo(s.id, s.name));
    }

    const result = await this.repository.findAll({ page, limit, sort, order, filters });
    return {
      data: result.data.map(s => new StickerResponseTo(s.id, s.name)),
      total: result.total,
      page: result.page,
      totalPages: result.totalPages
    };
  }

  async getById(id) {
    const sticker = await this.repository.findById(id);
    return new StickerResponseTo(sticker.id, sticker.name);
  }

  async create(stickerRequest) {
    const sticker = await this.repository.create({ name: stickerRequest.name });
    return new StickerResponseTo(sticker.id, sticker.name);
  }

  async update(id, stickerRequest) {
    const updated = await this.repository.update(id, { name: stickerRequest.name });
    return new StickerResponseTo(updated.id, updated.name);
  }

  async delete(id) {
    await this.repository.delete(id);
  }
}

module.exports = new StickerService();