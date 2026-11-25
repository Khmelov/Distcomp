export interface PaginationParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export interface IRepository<T> {
  findAll(params?: PaginationParams): Promise<T[]>;
  findById(id: number): Promise<T | undefined>;
  save(entity: Omit<T, 'id'>): Promise<T>;
  update(id: number, entity: Partial<T>): Promise<T | undefined>;
  delete(id: number): Promise<boolean>;
  existsById(id: number): Promise<boolean>;
}
