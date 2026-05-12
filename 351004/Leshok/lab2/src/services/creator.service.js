const BaseRepository = require('../repositories/base.repository');
const { Creator } = require('../models');
const { CreatorResponseTo } = require('../dtos/creator.dto');

class CreatorService {
  constructor() {
    this.repository = new BaseRepository(Creator);
  }

  async getAll(page, limit, sort, order, filters) {
    if (page === undefined && limit === undefined) {
      const all = await Creator.findAll({
        where: filters,
        order: [[sort || 'id', (order || 'ASC').toUpperCase()]]
      });
      return all.map(c => new CreatorResponseTo(c.id, c.login, c.firstname, c.lastname));
    }

    const result = await this.repository.findAll({ page, limit, sort, order, filters });
    return {
      data: result.data.map(c => new CreatorResponseTo(c.id, c.login, c.firstname, c.lastname)),
      total: result.total,
      page: result.page,
      totalPages: result.totalPages
    };
  }

  async getById(id) {
    const creator = await this.repository.findById(id);
    return new CreatorResponseTo(creator.id, creator.login, creator.firstname, creator.lastname);
  }

  async create(creatorRequest) {
    const creator = await this.repository.create({
      login: creatorRequest.login,
      password: creatorRequest.password,
      firstname: creatorRequest.firstname,
      lastname: creatorRequest.lastname
    });
    return new CreatorResponseTo(creator.id, creator.login, creator.firstname, creator.lastname);
  }

  async update(id, creatorRequest) {
    const updated = await this.repository.update(id, {
      login: creatorRequest.login,
      password: creatorRequest.password,
      firstname: creatorRequest.firstname,
      lastname: creatorRequest.lastname
    });
    return new CreatorResponseTo(updated.id, updated.login, updated.firstname, updated.lastname);
  }

  async delete(id) {
    await this.repository.delete(id);
  }
}

module.exports = new CreatorService();