import { TopicRequestTo } from '../dtos/requests';
import { TopicResponseTo } from '../dtos/responses';
import { topicRequestSchema } from '../dtos/validation';
import { NotFoundError, UnprocessableEntityError } from '../errors/AppError';
import { PaginationParams } from '../repositories/IRepository';
import { PrismaAuthorRepository } from '../repositories/PrismaAuthorRepository';
import { PrismaTopicRepository } from '../repositories/PrismaTopicRepository';
import { BaseService } from './BaseService';
import { toTopicResponse } from './mappers';

export class TopicService extends BaseService {
  constructor(
    private readonly repository: PrismaTopicRepository,
    private readonly authorRepository: PrismaAuthorRepository
  ) {
    super('topic');
  }

  async create(dto: TopicRequestTo): Promise<TopicResponseTo> {
    const data = this.validate(topicRequestSchema, dto);
    if (!(await this.authorRepository.existsById(data.authorId))) {
      throw new UnprocessableEntityError(
        `Author with id ${data.authorId} does not exist`,
        '42202'
      );
    }

    const saved = await this.repository.save({
      ...data,
      created: new Date(),
      modified: new Date(),
    });
    return toTopicResponse(saved);
  }

  async findAll(params?: PaginationParams): Promise<TopicResponseTo[]> {
    const topics = await this.repository.findAll(params);
    return topics.map(toTopicResponse);
  }

  async findById(id: number): Promise<TopicResponseTo> {
    const entity = await this.repository.findById(id);
    if (!entity) {
      throw new NotFoundError(`Topic with id ${id} not found`, '40402');
    }
    return toTopicResponse(entity);
  }

  async update(id: number, dto: TopicRequestTo): Promise<TopicResponseTo> {
    const data = this.validate(topicRequestSchema, dto);
    const existing = await this.repository.findById(id);
    if (!existing) {
      throw new NotFoundError(`Topic with id ${id} not found`, '40402');
    }
    if (!(await this.authorRepository.existsById(data.authorId))) {
      throw new UnprocessableEntityError(
        `Author with id ${data.authorId} does not exist`,
        '42202'
      );
    }
    const updated = await this.repository.update(id, {
      ...data,
      modified: new Date(),
    });
    return toTopicResponse(updated!);
  }

  async delete(id: number): Promise<void> {
    const success = await this.repository.delete(id);
    if (!success) {
      throw new NotFoundError(`Topic with id ${id} not found`, '40402');
    }
  }

  async findByFilters(filters: {
    markerNames?: string[];
    markerIds?: number[];
    authorLogin?: string;
    title?: string;
    content?: string;
  }): Promise<TopicResponseTo[]> {
    let topics = await this.repository.findAll();

    if (filters.authorLogin) {
      const author = await this.authorRepository.findByLogin(
        filters.authorLogin
      );
      if (!author) return [];
      topics = topics.filter((t) => t.authorId === author.id);
    }

    if (filters.title) {
      topics = topics.filter((t) => t.title.includes(filters.title!));
    }

    if (filters.content) {
      topics = topics.filter((t) => t.content.includes(filters.content!));
    }

    return topics.map(toTopicResponse);
  }
}
