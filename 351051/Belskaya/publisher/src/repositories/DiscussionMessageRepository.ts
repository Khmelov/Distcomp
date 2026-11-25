import { CACHE_PREFIX, CACHE_TTL } from '../constants';
import { redisClient } from '../db';
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
    const cacheKey = `${CACHE_PREFIX.MESSAGE}:all`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);

    try {
      const response = await sendKafkaRequest('GET_ALL', {});
      if (response.action === 'GET_ALL_ERROR') {
        handleKafkaError(new Error(response.error));
      }

      const messages = response.data as Message[];
      await redisClient.set(cacheKey, JSON.stringify(messages), {
        EX: CACHE_TTL,
      });

      return messages;
    } catch (err) {
      handleKafkaError(err);
    }
  }

  async findById(id: number): Promise<Message | undefined> {
    const cacheKey = `${CACHE_PREFIX.MESSAGE}:${id}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);

    try {
      const response = await sendKafkaRequest('GET_BY_ID', { id });
      if (response.action === 'GET_BY_ID_ERROR') {
        handleKafkaError(new Error(response.error));
      }

      const message = (response.data ?? undefined) as Message | undefined;

      if (message) {
        await redisClient.set(cacheKey, JSON.stringify(message), {
          EX: CACHE_TTL,
        });
      }

      return message;
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
    const cacheKey = `${CACHE_PREFIX.MESSAGE}:topic:${topicId}:${sortOrder ?? 'asc'}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);

    try {
      const response = await sendKafkaRequest('GET_BY_TOPIC', {
        topicId,
        sortOrder: sortOrder ?? 'asc',
      });
      if (response.action === 'GET_BY_TOPIC_ERROR') {
        handleKafkaError(new Error(response.error));
      }

      const messages = response.data as Message[];
      await redisClient.set(cacheKey, JSON.stringify(messages), {
        EX: CACHE_TTL,
      });

      return messages;
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

      const created = response.data as Message;

      await this.invalidateListCaches();
      await redisClient.set(
        `${CACHE_PREFIX.MESSAGE}:${created.id}`,
        JSON.stringify(created),
        {
          EX: CACHE_TTL,
        }
      );

      return created;
    } catch (err) {
      handleKafkaError(err);
    }
  }

  async update(
    id: number,
    entity: Partial<Message>
  ): Promise<Message | undefined> {
    try {
      const response = await sendKafkaRequest('UPDATE', { id, patch: entity });
      if (response.action === 'UPDATE_ERROR') {
        handleKafkaError(new Error(response.error));
      }

      const updated = (response.data ?? undefined) as Message | undefined;

      if (updated) {
        await redisClient.del(`${CACHE_PREFIX.MESSAGE}:${id}`);
        await this.invalidateListCaches();
      }

      return updated;
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

      const ok = Boolean(response.data);

      if (ok) {
        await redisClient.del(`${CACHE_PREFIX.MESSAGE}:${id}`);
        await this.invalidateListCaches();
      }

      return ok;
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

  private async invalidateListCaches(): Promise<void> {
    const allKeys = await redisClient.keys(`${CACHE_PREFIX.MESSAGE}:all`);
    const topicKeys = await redisClient.keys(`${CACHE_PREFIX.MESSAGE}:topic:*`);
    const keysToDelete = [...allKeys, ...topicKeys];

    if (keysToDelete.length > 0) {
      await redisClient.del(keysToDelete);
    }
  }
}
