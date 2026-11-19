export interface IRepository<T> {
  findAll(): T[];
  findById(id: number): T | undefined;
  save(entity: T): T;
  update(id: number, entity: Partial<T>): T | undefined;
  delete(id: number): boolean;
  existsById(id: number): boolean;
}