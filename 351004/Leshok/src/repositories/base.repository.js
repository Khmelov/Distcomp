class BaseRepository {
  constructor() {
    this.entities = new Map();
    this.currentId = 1;
  }

  findAll() {
    return Array.from(this.entities.values());
  }

  findById(id) {
    return this.entities.get(id) || null;
  }

  create(entity) {
    const id = this.currentId++;
    entity.id = id;
    this.entities.set(id, entity);
    return entity;
  }

  update(id, updatedEntity) {
    if (!this.entities.has(id)) return null;
    updatedEntity.id = id;
    this.entities.set(id, updatedEntity);
    return updatedEntity;
  }

  delete(id) {
    return this.entities.delete(id);
  }
}

module.exports = BaseRepository;