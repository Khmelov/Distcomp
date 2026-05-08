class BaseService {
  constructor(model) {
    this.model = model;
  }

  async create(data) {
    return this.model.create(data);
  }

  async findById(id) {
    return this.model.findByPk(id);
  }

  async update(id, data) {
    const entity = await this.findById(id);
    if (!entity) throw new Error('Not found');
    return entity.update(data);
  }

  async delete(id) {
    const entity = await this.findById(id);
    if (!entity) throw new Error('Not found');
    return entity.destroy();
  }

  async findAll({ page = 1, size = 10, sort = 'id' }) {
    return this.model.findAndCountAll({
      limit: size,
      offset: (page - 1) * size,
      order: [[sort, 'ASC']],
    });
  }
}

module.exports = BaseService;