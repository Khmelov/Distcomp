import bcrypt from 'bcrypt';
import { AuthorRequestTo } from '../dtos/requests';
import { AuthorResponseTo } from '../dtos/responses';
import { authorRequestSchema } from '../dtos/validation';
import { ConflictError, NotFoundError } from '../errors/AppError';
import { PaginationParams } from '../repositories/IRepository';
import { PrismaAuthorRepository } from '../repositories/PrismaAuthorRepository';
import { PrismaTopicRepository } from '../repositories/PrismaTopicRepository';
import { BaseService } from './BaseService';

export class AuthorService extends BaseService {
  constructor(
    private readonly repository: PrismaAuthorRepository,
    private readonly topicRepository: PrismaTopicRepository
  ) {
    super('author');
  }

  async create(dto: AuthorRequestTo): Promise<AuthorResponseTo> {
    const data = this.validate(authorRequestSchema, dto);
    if (await this.repository.findByLogin(data.login)) {
      throw new ConflictError(
        `Author with login '${data.login}' already exists`
      );
    }
    const hashedPassword = bcrypt.hashSync(data.password, 10);
    const saved = await this.repository.save({
      ...data,
      password: hashedPassword,
    });
    return saved;
  }

  async findAll(params?: PaginationParams): Promise<AuthorResponseTo[]> {
    const authors = await this.repository.findAll(params);
    return authors;
  }

  async findById(id: number): Promise<AuthorResponseTo> {
    const entity = await this.repository.findById(id);
    if (!entity) {
      throw new NotFoundError(`Author with id ${id} not found`);
    }
    return entity;
  }

  async update(id: number, dto: AuthorRequestTo): Promise<AuthorResponseTo> {
    const data = this.validate(authorRequestSchema, dto);
    const existing = await this.repository.findById(id);
    if (!existing) {
      throw new NotFoundError(`Author with id ${id} not found`);
    }
    const duplicate = await this.repository.findByLogin(data.login);
    if (duplicate && duplicate.id !== id) {
      throw new ConflictError(
        `Author with login '${data.login}' already exists`
      );
    }

    const updated = await this.repository.update(id, {
      ...data,
      password: bcrypt.hashSync(data.password, 10),
    });
    return updated!;
  }

  async delete(id: number): Promise<void> {
    const success = await this.repository.delete(id);
    if (!success) {
      throw new NotFoundError(`Author with id ${id} not found`);
    }
  }

  async findByTopicId(topicId: number): Promise<AuthorResponseTo> {
    const topic = await this.topicRepository.findById(topicId);
    if (!topic) {
      throw new NotFoundError(`Topic with id ${topicId} not found`, '40402');
    }

    const author = await this.repository.findById(topic.authorId);
    if (!author) {
      throw new NotFoundError(`Author for topic ${topicId} not found`, '40401');
    }

    return author;
  }
}
