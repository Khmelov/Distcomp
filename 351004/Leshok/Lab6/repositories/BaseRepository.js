// repositories/BaseRepository.js
class BaseRepository {
  constructor(store, idGenerator) {
    this.store = store;
    this.idGenerator = idGenerator;
  }

  findAll() {
    return Array.from(this.store.values());
  }

  findById(id) {
    return this.store.get(Number(id));
  }

  create(entity) {
    const id = this.idGenerator();
    const newEntity = { ...entity, id };
    this.store.set(id, newEntity);
    return newEntity;
  }

  update(id, data) {
    const existing = this.findById(id);
    if (!existing) return null;
    const updated = { ...existing, ...data, id: existing.id };
    this.store.set(id, updated);
    return updated;
  }

  delete(id) {
    return this.store.delete(Number(id));
  }
}

module.exports = BaseRepository;