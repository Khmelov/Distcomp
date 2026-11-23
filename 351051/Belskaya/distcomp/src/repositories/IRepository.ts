export interface IRepository<T> {
  findAll(): Promise<T[]>;
  findById(id: number): Promise<T | undefined>;
  save(entity: Omit<T, 'id'>): Promise<T>; // При создании id генерируется базой
  update(id: number, entity: Partial<T>): Promise<T | undefined>;
  delete(id: number): Promise<boolean>;
  existsById(id: number): Promise<boolean>;
}
