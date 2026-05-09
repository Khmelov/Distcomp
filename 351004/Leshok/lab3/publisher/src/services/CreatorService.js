const { Creator } = require('../models');
const { CreatorResponseDto } = require('../dtos/CreatorDto');

class CreatorService {
  async create(dto) {
    const { login, password, firstname, lastname } = dto;
    const creator = await Creator.create({ login, password, firstname, lastname });
    return new CreatorResponseDto(creator.id, creator.login, creator.password, creator.firstname, creator.lastname);
  }

  async update(id, dto) {
    const creator = await Creator.findByPk(id);
    if (!creator) throw { status: 404, code: '40401', message: 'Creator not found' };
    const { login, password, firstname, lastname } = dto;
    await creator.update({ login, password, firstname, lastname });
    return new CreatorResponseDto(creator.id, creator.login, creator.password, creator.firstname, creator.lastname);
  }

  async getById(id) {
    const creator = await Creator.findByPk(id);
    if (!creator) throw { status: 404, code: '40401', message: 'Creator not found' };
    return new CreatorResponseDto(creator.id, creator.login, creator.password, creator.firstname, creator.lastname);
  }

  async getAll() {
    const creators = await Creator.findAll();
    return creators.map(c => new CreatorResponseDto(c.id, c.login, c.password, c.firstname, c.lastname));
  }

  async delete(id) {
    const creator = await Creator.findByPk(id);
    if (!creator) throw { status: 404, code: '40401', message: 'Creator not found' };
    await creator.destroy();
  }
}

module.exports = new CreatorService();