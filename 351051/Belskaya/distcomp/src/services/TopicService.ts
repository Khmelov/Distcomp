import { TopicRequestTo } from '../dtos/requests';
import { TopicResponseTo } from '../dtos/responses';
import { topicRequestSchema } from '../dtos/validation';
import { NotFoundError, UnprocessableEntityError } from '../errors/AppError';
import { topicMarkerRepository, markerRepository } from '../repositories';
import { AuthorRepository } from '../repositories/AuthorRepository';
import { TopicRepository } from '../repositories/TopicRepository';
import { BaseService } from './BaseService';
import { toTopicEntity, toTopicResponse } from './mappers';

export class TopicService extends BaseService {
  constructor(
    private readonly repository: TopicRepository,
    private readonly authorRepository: AuthorRepository
  ) {
    super('topic');
  }

  create(dto: TopicRequestTo): TopicResponseTo {
    const data = this.validate(topicRequestSchema, dto);
    if (!this.authorRepository.existsById(data.authorId)) {
      throw new UnprocessableEntityError(
        `Author with id ${data.authorId} does not exist`,
        '42202'
      );
    }
    const now = new Date();
    const entity = toTopicEntity(data, this.generateId(), now, now);
    const saved = this.repository.save(entity);
    return toTopicResponse(saved);
  }

  findAll(): TopicResponseTo[] {
    return this.repository.findAll().map(toTopicResponse);
  }

  findById(id: number): TopicResponseTo {
    const entity = this.repository.findById(id);
    if (!entity) {
      throw new NotFoundError(`Topic with id ${id} not found`, '40402');
    }
    return toTopicResponse(entity);
  }

  update(id: number, dto: TopicRequestTo): TopicResponseTo {
    const data = this.validate(topicRequestSchema, dto);
    const existing = this.repository.findById(id);
    if (!existing) {
      throw new NotFoundError(`Topic with id ${id} not found`, '40402');
    }
    if (!this.authorRepository.existsById(data.authorId)) {
      throw new UnprocessableEntityError(
        `Author with id ${data.authorId} does not exist`,
        '42202'
      );
    }
    const updated = this.repository.update(id, {
      ...data,
      modified: new Date(),
    });
    return toTopicResponse(updated!);
  }

  delete(id: number): void {
    if (!this.repository.delete(id)) {
      throw new NotFoundError(`Topic with id ${id} not found`, '40402');
    }
  }

  findByFilters(filters: {
    markerNames?: string[];
    markerIds?: number[];
    authorLogin?: string;
    title?: string;
    content?: string;
  }): TopicResponseTo[] {
    let topics = this.repository.findAll();

    if (filters.authorLogin) {
      const author = this.authorRepository.findByLogin(filters.authorLogin);
      if (!author) return [];
      topics = topics.filter((t) => t.authorId === author.id);
    }

    if (filters.title) {
      topics = topics.filter((t) => t.title.includes(filters.title!));
    }

    if (filters.content) {
      topics = topics.filter((t) => t.content.includes(filters.content!));
    }

    if (filters.markerIds && filters.markerIds.length > 0) {
      const topicIdsWithMarkers = new Set(
        filters.markerIds.flatMap((id) =>
          topicMarkerRepository.getTopicIdsByMarkerId(id)
        )
      );
      topics = topics.filter((t) => topicIdsWithMarkers.has(t.id));
    }

    if (filters.markerNames && filters.markerNames.length > 0) {
      const markerIdsFromNames = filters.markerNames
        .map((name) => markerRepository.findByName(name)?.id)
        .filter((id): id is number => id !== undefined);

      if (markerIdsFromNames.length === 0) return []; // Если маркеров с такими именами нет

      const topicIdsWithMarkers = new Set(
        markerIdsFromNames.flatMap((id) =>
          topicMarkerRepository.getTopicIdsByMarkerId(id)
        )
      );
      topics = topics.filter((t) => topicIdsWithMarkers.has(t.id));
    }

    return topics.map(toTopicResponse);
  }
}
