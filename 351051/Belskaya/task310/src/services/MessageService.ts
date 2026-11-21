import { MessageRequestTo } from '../dtos/requests';
import { MessageResponseTo } from '../dtos/responses';
import { messageRequestSchema } from '../dtos/validation';
import { NotFoundError, UnprocessableEntityError } from '../errors/AppError';
import { MessageRepository } from '../repositories/MessageRepository';
import { TopicRepository } from '../repositories/TopicRepository';
import { BaseService } from './BaseService';
import { toMessageEntity, toMessageResponse } from './mappers';

export class MessageService extends BaseService {
  constructor(
    private readonly repository: MessageRepository,
    private readonly topicRepository: TopicRepository
  ) {
    super('message');
  }

  create(dto: MessageRequestTo): MessageResponseTo {
    const data = this.validate(messageRequestSchema, dto);
    if (!this.topicRepository.existsById(data.topicId)) {
      throw new UnprocessableEntityError(
        `Topic with id ${data.topicId} does not exist`,
        '42203'
      );
    }
    const entity = toMessageEntity(data, this.generateId());
    const saved = this.repository.save(entity);
    return toMessageResponse(saved);
  }

  findAll(): MessageResponseTo[] {
    return this.repository.findAll().map(toMessageResponse);
  }

  findById(id: number): MessageResponseTo {
    const entity = this.repository.findById(id);
    if (!entity) {
      throw new NotFoundError(`Message with id ${id} not found`, '40404');
    }
    return toMessageResponse(entity);
  }

  update(id: number, dto: MessageRequestTo): MessageResponseTo {
    const data = this.validate(messageRequestSchema, dto);
    const existing = this.repository.findById(id);
    if (!existing) {
      throw new NotFoundError(`Message with id ${id} not found`, '40404');
    }
    if (!this.topicRepository.existsById(data.topicId)) {
      throw new UnprocessableEntityError(
        `Topic with id ${data.topicId} does not exist`,
        '42203'
      );
    }
    const updated = this.repository.update(id, data);
    return toMessageResponse(updated!);
  }

  delete(id: number): void {
    if (!this.repository.delete(id)) {
      throw new NotFoundError(`Message with id ${id} not found`, '40404');
    }
  }
}