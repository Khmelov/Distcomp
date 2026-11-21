import { AuthorRequestTo } from '../dtos/requests';
import { AuthorResponseTo } from '../dtos/responses';
import { authorRequestSchema } from '../dtos/validation';
import { ConflictError, NotFoundError } from '../errors/AppError';
import { AuthorRepository } from '../repositories/AuthorRepository';
import { BaseService } from './BaseService';
import { toAuthorEntity, toAuthorResponse } from './mappers';

export class AuthorService extends BaseService {
  constructor(private readonly repository: AuthorRepository) {
    super('author');
  }

  create(dto: AuthorRequestTo): AuthorResponseTo {
    const data = this.validate(authorRequestSchema, dto);
    if (this.repository.findByLogin(data.login)) {
      throw new ConflictError(`Author with login '${data.login}' already exists`);
    }
    const entity = toAuthorEntity(data, this.generateId());
    const saved = this.repository.save(entity);
    return toAuthorResponse(saved);
  }

  findAll(): AuthorResponseTo[] {
    return this.repository.findAll().map(toAuthorResponse);
  }

  findById(id: number): AuthorResponseTo {
    const entity = this.repository.findById(id);
    if (!entity) {
      throw new NotFoundError(`Author with id ${id} not found`);
    }
    return toAuthorResponse(entity);
  }

  update(id: number, dto: AuthorRequestTo): AuthorResponseTo {
    const data = this.validate(authorRequestSchema, dto);
    const existing = this.repository.findById(id);
    if (!existing) {
      throw new NotFoundError(`Author with id ${id} not found`);
    }
    const duplicate = this.repository.findByLogin(data.login);
    if (duplicate && duplicate.id !== id) {
      throw new ConflictError(`Author with login '${data.login}' already exists`);
    }
    const updated = this.repository.update(id, data);
    return toAuthorResponse(updated!);
  }

  delete(id: number): void {
    if (!this.repository.delete(id)) {
      throw new NotFoundError(`Author with id ${id} not found`);
    }
  }
}