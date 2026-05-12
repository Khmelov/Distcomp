// services/StickerService.js
const stickerRepo = require('../repositories/StickerRepository');
const newsStickerRepo = require('../repositories/NewsStickerRepository');
const Sticker = require('../models/Sticker');
const { toResponse, fromRequest } = require('../dto/stickerDto');
const { ValidationError, NotFoundError } = require('../utils/errors');

class StickerService {
  async getAll() {
    return stickerRepo.findAll().map(toResponse);
  }

  async getById(id) {
    const sticker = stickerRepo.findById(id);
    if (!sticker) throw new NotFoundError('Sticker не найден');
    return toResponse(sticker);
  }

  async create(stickerRequest) {
    const { name } = fromRequest(stickerRequest);
    if (!name || name.trim() === '') {
      throw new ValidationError('Name не может быть пустым');
    }
    // Проверка уникальности имени (опционально)
    const existing = stickerRepo.findAll().find(s => s.name === name);
    if (existing) throw new ValidationError('Sticker с таким именем уже существует', 409);
    const sticker = new Sticker(null, name);
    const saved = stickerRepo.create(sticker);
    return toResponse(saved);
  }

  async update(id, stickerRequest) {
    const existing = stickerRepo.findById(id);
    if (!existing) throw new NotFoundError('Sticker не найден');
    const updateData = {};
    if (stickerRequest.name !== undefined) {
      if (stickerRequest.name.trim() === '') throw new ValidationError('Name не может быть пустым');
      updateData.name = stickerRequest.name;
    }
    const updated = stickerRepo.update(id, updateData);
    return toResponse(updated);
  }

  async delete(id) {
    const existing = stickerRepo.findById(id);
    if (!existing) throw new NotFoundError('Sticker не найден');
    // Удаляем связи с новостями
    const newsIds = newsStickerRepo.getNewsIdsByStickerId(id);
    for (let newsId of newsIds) {
      newsStickerRepo.removeStickerFromNews(newsId, id);
    }
    stickerRepo.delete(id);
    return true;
  }

  // Дополнительно: получить стикеры по id новости (для эндпоинта /news/:id/stickers)
  async getByNewsId(newsId) {
    const stickerIds = newsStickerRepo.getStickersByNewsId(newsId);
    const stickers = stickerIds.map(id => stickerRepo.findById(id)).filter(s => s);
    return stickers.map(toResponse);
  }
}

module.exports = new StickerService();