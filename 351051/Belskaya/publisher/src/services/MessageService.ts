import { MessageRequestTo } from '../dtos/requests';
import { MessageResponseTo } from '../dtos/responses';
import { messageRequestSchema } from '../dtos/validation';
import { NotFoundError, UnprocessableEntityError } from '../errors/AppError';
import { PaginationParams } from '../repositories/IRepository';
import { PrismaMessageRepository } from '../repositories/PrismaMessageRepository';
import { PrismaTopicRepository } from '../repositories/PrismaTopicRepository';
import { BaseService } from './BaseService';

export class MessageService extends BaseService {
  constructor(
    private readonly repository: PrismaMessageRepository,
    private readonly topicRepository: PrismaTopicRepository
  ) {
    super('message');
  }

  async create(dto: MessageRequestTo): Promise<MessageResponseTo> {
    const data = this.validate(messageRequestSchema, dto);
    if (!(await this.topicRepository.existsById(data.topicId))) {
      throw new UnprocessableEntityError(
        `Topic with id ${data.topicId} does not exist`,
        '42203'
      );
    }

    const saved = await this.repository.save(data);
    return saved;
  }

  async findAll(params?: PaginationParams): Promise<MessageResponseTo[]> {
    const messages = await this.repository.findAll(params);
    return messages;
  }

  async findById(id: number): Promise<MessageResponseTo> {
    const entity = await this.repository.findById(id);
    if (!entity) {
      throw new NotFoundError(`Message with id ${id} not found`, '40404');
    }
    return entity;
  }

  async update(id: number, dto: MessageRequestTo): Promise<MessageResponseTo> {
    const data = this.validate(messageRequestSchema, dto);
    const existing = await this.repository.findById(id);
    if (!existing) {
      throw new NotFoundError(`Message with id ${id} not found`, '40404');
    }
    if (!(await this.topicRepository.existsById(data.topicId))) {
      throw new UnprocessableEntityError(
        `Topic with id ${data.topicId} does not exist`,
        '42203'
      );
    }

    const updated = await this.repository.update(id, data);
    return updated!;
  }

  async delete(id: number): Promise<void> {
    const success = await this.repository.delete(id);
    if (!success) {
      throw new NotFoundError(`Message with id ${id} not found`, '40404');
    }
  }

  async findByTopicId(topicId: number): Promise<MessageResponseTo[]> {
    if (!(await this.topicRepository.existsById(topicId))) {
      throw new NotFoundError(`Topic with id ${topicId} not found`, '40402');
    }
    const messages = await this.repository.findByTopicId(topicId);
    return messages;
  }
}
