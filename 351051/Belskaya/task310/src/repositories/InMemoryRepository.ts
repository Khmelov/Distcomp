import { IRepository } from './IRepository';

export abstract class InMemoryRepository<T extends { id: number }>
  implements IRepository<T>
{
  protected readonly items: Map<number, T> = new Map();

  findAll(): T[] {
    return Array.from(this.items.values());
  }

  findById(id: number): T | undefined {
    return this.items.get(id);
  }

  save(entity: T): T {
    this.items.set(entity.id, entity);
    return entity;
  }

  update(id: number, entity: Partial<T>): T | undefined {
    const existing = this.items.get(id);
    if (!existing) {
      return undefined;
    }
    const updated = { ...existing, ...entity, id };
    this.items.set(id, updated);
    return updated;
  }

  delete(id: number): boolean {
    return this.items.delete(id);
  }

  existsById(id: number): boolean {
    return this.items.has(id);
  }
}