import { Message } from '../entities';
import {
  AppError,
  ConflictError,
  NotFoundError,
  UnprocessableEntityError,
  ValidationError,
} from '../errors/AppError';
import { sendKafkaRequest } from '../kafka/requestReply';
import { IRepository, PaginationParams } from './IRepository';

function handleKafkaError(err: unknown): never {
  const message = (err as Error).message ?? 'Kafka error';

  if (message.includes('timeout')) {
    throw new AppError(504, '50401', `Discussion timeout: ${message}`);
  }
  if (message.toLowerCase().includes('not found')) {
    throw new NotFoundError(message);
  }
  if (message.includes('400')) {
    throw new ValidationError(message);
  }
  if (message.includes('403')) {
    throw new ConflictError(message);
  }
  if (message.includes('422')) {
    throw new UnprocessableEntityError(message);
  }
  throw new AppError(500, '50001', message);
}

export class DiscussionMessageRepository implements IRepository<Message> {
  async findAll(): Promise<Message[]> {
    try {
      const response = await sendKafkaRequest('GET_ALL', {});
      if (response.action === 'GET_ALL_ERROR') {
        handleKafkaError(new Error(response.error));
      }
      return response.data as Message[];
    } catch (err) {
      handleKafkaError(err);
    }
  }

  async findById(id: number): Promise<Message | undefined> {
    try {
      const response = await sendKafkaRequest('GET_BY_ID', { id });
      if (response.action === 'GET_BY_ID_ERROR') {
        handleKafkaError(new Error(response.error));
      }
      return (response.data ?? undefined) as Message | undefined;
    } catch (err) {
      if ((err as Error).message?.toLowerCase().includes('not found')) {
        return undefined;
      }
      handleKafkaError(err);
    }
  }

  async findByTopicId(
    topicId: number,
    sortOrder?: PaginationParams['sortOrder']
  ): Promise<Message[]> {
    try {
      const response = await sendKafkaRequest('GET_BY_TOPIC', {
        topicId,
        sortOrder: sortOrder ?? 'asc',
      });
      if (response.action === 'GET_BY_TOPIC_ERROR') {
        handleKafkaError(new Error(response.error));
      }
      return response.data as Message[];
    } catch (err) {
      handleKafkaError(err);
    }
  }

  async save(entity: Omit<Message, 'id'>): Promise<Message> {
    const id = Date.now();
    const payload = { ...entity, id, state: 'PENDING' as const };

    try {
      const response = await sendKafkaRequest('CREATE', payload);
      if (response.action === 'CREATE_ERROR') {
        handleKafkaError(new Error(response.error));
      }
      return response.data as Message;
    } catch (err) {
      handleKafkaError(err);
    }
  }

  async update(
    id: number,
    entity: Partial<Message>
  ): Promise<Message | undefined> {
    try {
      const response = await sendKafkaRequest('UPDATE', {
        id,
        patch: entity,
      });
      if (response.action === 'UPDATE_ERROR') {
        handleKafkaError(new Error(response.error));
      }
      return (response.data ?? undefined) as Message | undefined;
    } catch (err) {
      if ((err as Error).message?.toLowerCase().includes('not found')) {
        return undefined;
      }
      handleKafkaError(err);
    }
  }

  async delete(id: number): Promise<boolean> {
    try {
      const response = await sendKafkaRequest('DELETE', { id });
      if (response.action === 'DELETE_ERROR') {
        handleKafkaError(new Error(response.error));
      }
      return Boolean(response.data);
    } catch (err) {
      if ((err as Error).message?.toLowerCase().includes('not found')) {
        return false;
      }
      handleKafkaError(err);
    }
  }

  async existsById(id: number): Promise<boolean> {
    return (await this.findById(id)) !== undefined;
  }
}
