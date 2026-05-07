const stickerRepo = require('../repositories/sticker.repository');
const { AppError } = require('../utils/errors');
const { mapToStickerResponse, mapToStickerEntity } = require('../dtos/sticker.dto');

class StickerService {
  getAll() {
    const stickers = stickerRepo.findAll();
    return stickers.map(mapToStickerResponse);
  }

  getById(id) {
    const sticker = stickerRepo.findById(Number(id));
    if (!sticker) {
      throw new AppError('Sticker not found', 404, 40430);
    }
    return mapToStickerResponse(sticker);
  }

  create(data) {
    this.validateStickerData(data);
    const existing = stickerRepo.findAll().find(s => s.name === data.name);
    if (existing) {
      throw new AppError('Sticker name already exists', 400, 40031);
    }
    const newSticker = mapToStickerEntity(data);
    const created = stickerRepo.create(newSticker);
    return mapToStickerResponse(created);
  }

  update(id, data) {
    const existing = stickerRepo.findById(Number(id));
    if (!existing) {
      throw new AppError('Sticker not found', 404, 40430);
    }
    this.validateStickerData(data, true);
    if (data.name && data.name !== existing.name) {
      const duplicate = stickerRepo.findAll().find(s => s.name === data.name);
      if (duplicate) {
        throw new AppError('Sticker name already exists', 400, 40031);
      }
    }
    const updatedSticker = { ...existing, ...data };
    const result = stickerRepo.update(Number(id), updatedSticker);
    return mapToStickerResponse(result);
  }

  delete(id) {
    const deleted = stickerRepo.delete(Number(id));
    if (!deleted) {
      throw new AppError('Sticker not found', 404, 40430);
    }
    return true;
  }

  validateStickerData(data, isUpdate = false) {
    const { name } = data;
    if (!isUpdate && !name) {
      throw new AppError('Missing required field: name', 400, 40032);
    }
    if (name && (name.length < 2 || name.length > 32)) {
      throw new AppError('Name must be between 2 and 32 characters', 400, 40033);
    }
  }
}

module.exports = new StickerService();