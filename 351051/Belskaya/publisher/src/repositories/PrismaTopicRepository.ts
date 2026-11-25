import { CACHE_PREFIX, CACHE_TTL } from '../constants';
import { prisma, redisClient } from '../db';
import { Topic } from '../entities';
import { getPaginationCacheKey } from '../utils/getCacheKey';
import { getPaginationOptions } from '../utils/getPaginationOptions';
import { IRepository, PaginationParams } from './IRepository';

export class PrismaTopicRepository implements IRepository<Topic> {
  async findAll(params?: PaginationParams): Promise<Topic[]> {
    const cacheKey = `${CACHE_PREFIX.TOPIC}:all:${getPaginationCacheKey(params)}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);
    const topics = await prisma.topic.findMany(getPaginationOptions(params));

    await redisClient.set(cacheKey, JSON.stringify(topics), { EX: CACHE_TTL });

    return topics;
  }

  async findById(id: number): Promise<Topic | undefined> {
    const cacheKey = `${CACHE_PREFIX.TOPIC}:${id}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);
    const topic = await prisma.topic.findUnique({ where: { id } });

    if (topic) {
      await redisClient.set(cacheKey, JSON.stringify(topic), { EX: CACHE_TTL });
    }

    return topic || undefined;
  }

  async findByAuthorId(authorId: number): Promise<Topic[]> {
    const cacheKey = `${CACHE_PREFIX.TOPIC}:author:${authorId}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);
    const topics = await prisma.topic.findMany({ where: { authorId } });

    await redisClient.set(cacheKey, JSON.stringify(topics), { EX: CACHE_TTL });

    return topics;
  }

  async save(entity: Omit<Topic, 'id'>): Promise<Topic> {
    const topic = await prisma.topic.create({ data: entity });

    await this.invalidateListCaches();
    await redisClient.del(`${CACHE_PREFIX.TOPIC}:author:${entity.authorId}`);

    return topic;
  }

  async saveWithMarkers(
    entity: Omit<Topic, 'id'>,
    markerNames: string[]
  ): Promise<Topic> {
    const topic = await prisma.$transaction(async (tx) => {
      const created = await tx.topic.create({ data: entity });

      const markers = await Promise.all(
        markerNames.map((name) =>
          tx.marker.upsert({
            where: { name },
            update: {},
            create: { name },
          })
        )
      );

      if (markers.length > 0) {
        await tx.topicMarker.createMany({
          data: markers.map((marker) => ({
            topicId: created.id,
            markerId: marker.id,
          })),
          skipDuplicates: true,
        });
      }

      return created;
    });

    await this.invalidateListCaches();
    await redisClient.del(`${CACHE_PREFIX.TOPIC}:author:${entity.authorId}`);

    return topic;
  }

  async update(id: number, entity: Partial<Topic>): Promise<Topic | undefined> {
    try {
      const topic = await prisma.topic.update({ where: { id }, data: entity });

      await redisClient.del(`${CACHE_PREFIX.TOPIC}:${id}`);
      await this.invalidateListCaches();
      if (topic.authorId) {
        await redisClient.del(`${CACHE_PREFIX.TOPIC}:author:${topic.authorId}`);
      }

      return topic;
    } catch {
      return undefined;
    }
  }

  async delete(id: number): Promise<boolean> {
    try {
      const topic = await prisma.topic.findUnique({ where: { id } });
      const authorId = topic?.authorId;

      await prisma.$transaction(async (tx) => {
        const topicMarkers = await tx.topicMarker.findMany({
          where: { topicId: id },
          select: { markerId: true },
        });

        await tx.topic.delete({ where: { id } });

        if (topicMarkers.length > 0) {
          await tx.marker.deleteMany({
            where: {
              id: { in: topicMarkers.map((item) => item.markerId) },
              topics: { none: {} },
            },
          });
        }
      });

      await redisClient.del(`${CACHE_PREFIX.TOPIC}:${id}`);
      await this.invalidateListCaches();
      if (authorId) {
        await redisClient.del(`${CACHE_PREFIX.TOPIC}:author:${authorId}`);
      }

      return true;
    } catch {
      return false;
    }
  }

  async existsById(id: number): Promise<boolean> {
    return (await prisma.topic.count({ where: { id } })) > 0;
  }

  async existsByTitle(title: string): Promise<boolean> {
    const count = await prisma.topic.count({ where: { title } });
    return count > 0;
  }

  private async invalidateListCaches(): Promise<void> {
    const keys = await redisClient.keys(`${CACHE_PREFIX.TOPIC}:all:*`);
    if (keys.length > 0) {
      await redisClient.del(keys);
    }
  }
}
