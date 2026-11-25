import { MarkerRequestTo } from '../dtos/requests';
import { MarkerResponseTo } from '../dtos/responses';
import { markerRequestSchema } from '../dtos/validation';
import { NotFoundError } from '../errors/AppError';
import { PaginationParams } from '../repositories/IRepository';
import { PrismaMarkerRepository } from '../repositories/PrismaMarkerRepository';
import { BaseService } from './BaseService';

export class MarkerService extends BaseService {
  constructor(private readonly repository: PrismaMarkerRepository) {
    super('marker');
  }

  async create(dto: MarkerRequestTo): Promise<MarkerResponseTo> {
    const data = this.validate(markerRequestSchema, dto);
    const saved = await this.repository.save(data);
    return saved;
  }

  async findAll(params?: PaginationParams): Promise<MarkerResponseTo[]> {
    const markers = await this.repository.findAll(params);
    return markers;
  }

  async findById(id: number): Promise<MarkerResponseTo> {
    const entity = await this.repository.findById(id);
    if (!entity)
      throw new NotFoundError(`Marker with id ${id} not found`, '40403');
    return entity;
  }

  async update(id: number, dto: MarkerRequestTo): Promise<MarkerResponseTo> {
    const data = this.validate(markerRequestSchema, dto);
    const updated = await this.repository.update(id, data);
    if (!updated)
      throw new NotFoundError(`Marker with id ${id} not found`, '40403');
    return updated;
  }

  async delete(id: number): Promise<void> {
    const success = await this.repository.delete(id);
    if (!success)
      throw new NotFoundError(`Marker with id ${id} not found`, '40403');
  }

  async findByTopicId(topicId: number): Promise<MarkerResponseTo[]> {
    const markers = await this.repository.findByTopicId(topicId);
    return markers;
  }
}
