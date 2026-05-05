class BaseRepository {
  constructor(model) {
    this.model = model;
  }

  async findAll({ page = 1, limit = 10, sort = 'id', order = 'ASC', filters = {} } = {}) {
    const offset = (page - 1) * limit;
    const where = {};
    for (const [key, value] of Object.entries(filters)) {
      if (value !== undefined && value !== null) where[key] = value;
    }
    const { count, rows } = await this.model.findAndCountAll({
      where,
      order: [[sort, order.toUpperCase()]],
      limit,
      offset,
      distinct: true
    });
    return {
      data: rows,
      total: count,
      page,
      totalPages: Math.ceil(count / limit)
    };
  }

  async findById(id) {
    const entity = await this.model.findByPk(id);
    if (!entity) throw new Error('Entity not found');
    return entity;
  }

  async create(data) {
    return await this.model.create(data);
  }

  async update(id, data) {
    const entity = await this.findById(id);
    await entity.update(data);
    return entity;
  }

  async delete(id) {
    const entity = await this.findById(id);
    await entity.destroy();
    return true;
  }
}

module.exports = BaseRepository;