import { MarkerRequestTo } from '../dtos/requests';
import { MarkerResponseTo } from '../dtos/responses';
import { markerRequestSchema } from '../dtos/validation';
import { ConflictError, NotFoundError } from '../errors/AppError';
import { MarkerRepository } from '../repositories/MarkerRepository';
import { BaseService } from './BaseService';
import { toMarkerEntity, toMarkerResponse } from './mappers';

export class MarkerService extends BaseService {
  constructor(private readonly repository: MarkerRepository) {
    super('marker');
  }

  create(dto: MarkerRequestTo): MarkerResponseTo {
    const data = this.validate(markerRequestSchema, dto);
    if (this.repository.findByName(data.name)) {
      throw new ConflictError(`Marker with name '${data.name}' already exists`);
    }
    const entity = toMarkerEntity(data, this.generateId());
    const saved = this.repository.save(entity);
    return toMarkerResponse(saved);
  }

  findAll(): MarkerResponseTo[] {
    return this.repository.findAll().map(toMarkerResponse);
  }

  findById(id: number): MarkerResponseTo {
    const entity = this.repository.findById(id);
    if (!entity) {
      throw new NotFoundError(`Marker with id ${id} not found`, '40403');
    }
    return toMarkerResponse(entity);
  }

  update(id: number, dto: MarkerRequestTo): MarkerResponseTo {
    const data = this.validate(markerRequestSchema, dto);
    const existing = this.repository.findById(id);
    if (!existing) {
      throw new NotFoundError(`Marker with id ${id} not found`, '40403');
    }
    const duplicate = this.repository.findByName(data.name);
    if (duplicate && duplicate.id !== id) {
      throw new ConflictError(`Marker with name '${data.name}' already exists`);
    }
    const updated = this.repository.update(id, data);
    return toMarkerResponse(updated!);
  }

  delete(id: number): void {
    if (!this.repository.delete(id)) {
      throw new NotFoundError(`Marker with id ${id} not found`, '40403');
    }
  }
}