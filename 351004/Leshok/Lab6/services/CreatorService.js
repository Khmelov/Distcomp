// services/CreatorService.js
const bcrypt = require('bcrypt');
const creatorRepo = require('../repositories/CreatorRepository');
const Creator = require('../models/Creator');
const { toResponse, fromRequest } = require('../dto/creatorDto');
const { ValidationError, NotFoundError } = require('../utils/errors');

class CreatorService {
  async getAll() {
    return creatorRepo.findAll().map(toResponse);
  }

  async getById(id) {
    const creator = creatorRepo.findById(id);
    if (!creator) throw new NotFoundError('Creator не найден');
    return toResponse(creator);
  }

  async findByLoginRaw(login) {
    return creatorRepo.findByLogin(login);
  }

  async create(creatorRequest) {
    // Валидация уникальности login
    const existing = creatorRepo.findByLogin(creatorRequest.login);
    if (existing) throw new ValidationError('Login уже существует', 409);
    
    const { login, password, firstName, lastName, role } = fromRequest(creatorRequest);
    const hashedPassword = await bcrypt.hash(password, 10);
    const creator = new Creator(null, login, hashedPassword, firstName, lastName, role);
    const saved = creatorRepo.create(creator);
    return toResponse(saved);
  }

  async update(id, creatorRequest) {
    const existing = creatorRepo.findById(id);
    if (!existing) throw new NotFoundError('Creator не найден');
    
    const updateData = {};
    if (creatorRequest.firstName !== undefined) updateData.firstName = creatorRequest.firstName;
    if (creatorRequest.lastName !== undefined) updateData.lastName = creatorRequest.lastName;
    if (creatorRequest.login !== undefined && creatorRequest.login !== existing.login) {
      const loginExists = creatorRepo.findByLogin(creatorRequest.login);
      if (loginExists && loginExists.id != id) throw new ValidationError('Login уже используется', 409);
      updateData.login = creatorRequest.login;
    }
    if (creatorRequest.password) updateData.password = await bcrypt.hash(creatorRequest.password, 10);
    if (creatorRequest.role) updateData.role = creatorRequest.role;
    
    const updated = creatorRepo.update(id, updateData);
    return toResponse(updated);
  }

  async delete(id) {
    const deleted = creatorRepo.delete(id);
    if (!deleted) throw new NotFoundError('Creator не найден');
    return true;
  }
}

module.exports = new CreatorService();