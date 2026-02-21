const creatorRepo = require('../repositories/creator.repository');
const { AppError } = require('../utils/errors');
const { mapToCreatorResponse, mapToCreatorEntity } = require('../dtos/creator.dto');

class CreatorService {
  getAll() {
    const creators = creatorRepo.findAll();
    return creators.map(mapToCreatorResponse);
  }

  getById(id) {
    const creator = creatorRepo.findById(Number(id));
    if (!creator) {
      throw new AppError('Creator not found', 404, 40401);
    }
    return mapToCreatorResponse(creator);
  }

  create(data) {
    this.validateCreatorData(data);
    const existing = creatorRepo.findAll().find(c => c.login === data.login);
    if (existing) {
      throw new AppError('Login already exists', 400, 40001);
    }
    const newCreator = mapToCreatorEntity(data);
    newCreator.created = new Date().toISOString();
    newCreator.modified = new Date().toISOString();
    const created = creatorRepo.create(newCreator);
    return mapToCreatorResponse(created);
  }

  update(id, data) {
    const existing = creatorRepo.findById(Number(id));
    if (!existing) {
      throw new AppError('Creator not found', 404, 40401);
    }
    this.validateCreatorData(data, true);
    if (data.login && data.login !== existing.login) {
      const duplicate = creatorRepo.findAll().find(c => c.login === data.login);
      if (duplicate) {
        throw new AppError('Login already exists', 400, 40001);
      }
    }
    const updatedCreator = { ...existing, ...data, modified: new Date().toISOString() };
    const result = creatorRepo.update(Number(id), updatedCreator);
    return mapToCreatorResponse(result);
  }

  delete(id) {
    const deleted = creatorRepo.delete(Number(id));
    if (!deleted) {
      throw new AppError('Creator not found', 404, 40401);
    }
    return true;
  }

  validateCreatorData(data, isUpdate = false) {
    const { login, password, firstname, lastname } = data;
    if (!isUpdate) {
      if (!login || !password || !firstname || !lastname) {
        throw new AppError('Missing required fields', 400, 40002);
      }
    }
    if (login && (login.length < 2 || login.length > 64)) {
      throw new AppError('Login must be between 2 and 64 characters', 400, 40003);
    }
    if (password && (password.length < 8 || password.length > 128)) {
      throw new AppError('Password must be between 8 and 128 characters', 400, 40004);
    }
    if (firstname && (firstname.length < 2 || firstname.length > 64)) {
      throw new AppError('Firstname must be between 2 and 64 characters', 400, 40005);
    }
    if (lastname && (lastname.length < 2 || lastname.length > 64)) {
      throw new AppError('Lastname must be between 2 and 64 characters', 400, 40006);
    }
  }
}

module.exports = new CreatorService();