import { CACHE_PREFIX, CACHE_TTL } from '../constants';
import { prisma, redisClient } from '../db';
import { Marker } from '../entities';
import { getPaginationCacheKey } from '../utils/getCacheKey';
import { getPaginationOptions } from '../utils/getPaginationOptions';
import { IRepository, PaginationParams } from './IRepository';

export class PrismaMarkerRepository implements IRepository<Marker> {
  async findAll(params?: PaginationParams): Promise<Marker[]> {
    const cacheKey = `${CACHE_PREFIX.MARKER}:all:${getPaginationCacheKey(params)}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);
    const markers = await prisma.marker.findMany(getPaginationOptions(params));

    await redisClient.set(cacheKey, JSON.stringify(markers), { EX: CACHE_TTL });

    return markers;
  }

  async findById(id: number): Promise<Marker | undefined> {
    const cacheKey = `${CACHE_PREFIX.MARKER}:${id}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);
    const marker = await prisma.marker.findUnique({ where: { id } });

    if (marker) {
      await redisClient.set(cacheKey, JSON.stringify(marker), {
        EX: CACHE_TTL,
      });
    }

    return marker || undefined;
  }

  async findByTopicId(topicId: number): Promise<Marker[]> {
    const cacheKey = `${CACHE_PREFIX.MARKER}:topic:${topicId}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);
    const markers = await prisma.marker.findMany({
      where: { topics: { some: { topicId } } },
    });

    await redisClient.set(cacheKey, JSON.stringify(markers), { EX: CACHE_TTL });

    return markers;
  }

  async save(entity: Omit<Marker, 'id'>): Promise<Marker> {
    const marker = await prisma.marker.create({ data: entity });

    await this.invalidateListCaches();

    return marker;
  }

  async update(
    id: number,
    entity: Partial<Marker>
  ): Promise<Marker | undefined> {
    try {
      const marker = await prisma.marker.update({
        where: { id },
        data: entity,
      });

      await redisClient.del(`${CACHE_PREFIX.MARKER}:${id}`);
      await this.invalidateListCaches();

      return marker;
    } catch {
      return undefined;
    }
  }

  async delete(id: number): Promise<boolean> {
    try {
      await prisma.marker.delete({ where: { id } });

      await redisClient.del(`${CACHE_PREFIX.MARKER}:${id}`);
      await this.invalidateListCaches();

      return true;
    } catch {
      return false;
    }
  }

  async existsById(id: number): Promise<boolean> {
    return (await prisma.marker.count({ where: { id } })) > 0;
  }

  private async invalidateListCaches(): Promise<void> {
    const keys = await redisClient.keys(`${CACHE_PREFIX.MARKER}:all*`);
    if (keys.length > 0) {
      await redisClient.del(keys);
    }
  }
}
