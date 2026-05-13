import { Repository, FindOptionsWhere, ObjectLiteral, DeepPartial } from 'typeorm';

export interface PaginationOptions {
  page: number;
  limit: number;
}

export interface SortOptions {
  field: string;
  order: 'ASC' | 'DESC';
}

export interface FilterOptions {
  [key: string]: any;
}

export abstract class BaseRepository<T extends ObjectLiteral> {
  constructor(protected readonly repository: Repository<T>) {}

  async create(data: Partial<T>): Promise<T> {
    const entity = this.repository.create(data as DeepPartial<T>);
    return this.repository.save(entity);
  }

  async update(id: number, data: Partial<T>): Promise<T> {
    await this.repository.update(id, data as any);
    const updated = await this.findById(id);
    if (!updated) throw new Error('Entity not found after update');
    return updated;
  }

  async delete(id: number): Promise<void> {
    await this.repository.delete(id);
  }

  async findById(id: number): Promise<T | null> {
    return this.repository.findOneBy({ id } as any);
  }

  async findAll(
    pagination?: PaginationOptions,
    sort?: SortOptions,
    filters?: FilterOptions,
  ): Promise<[T[], number]> {
    const { page = 1, limit = 10 } = pagination || {};
    const skip = (page - 1) * limit;

    const where: FindOptionsWhere<T> = {};
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          (where as any)[key] = value;
        }
      });
    }

    const order: any = {};
    if (sort) {
      order[sort.field] = sort.order;
    }

    return this.repository.findAndCount({
      where,
      order,
      skip,
      take: limit,
    });
  }
}