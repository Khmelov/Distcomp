import { CACHE_PREFIX, CACHE_TTL } from '../constants';
import { prisma, redisClient } from '../db';
import { Author } from '../entities';
import { getPaginationCacheKey } from '../utils/getCacheKey';
import { getPaginationOptions } from '../utils/getPaginationOptions';
import { IRepository, PaginationParams } from './IRepository';

export class PrismaAuthorRepository implements IRepository<Author> {
  async findAll(params?: PaginationParams): Promise<Author[]> {
    const cacheKey = `${CACHE_PREFIX.AUTHOR}:all:${getPaginationCacheKey(params)}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);
    const authors = await prisma.author.findMany(getPaginationOptions(params));

    await redisClient.set(cacheKey, JSON.stringify(authors), { EX: CACHE_TTL });

    return authors;
  }

  async findById(id: number): Promise<Author | undefined> {
    const cacheKey = `${CACHE_PREFIX.AUTHOR}:${id}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);
    const author = await prisma.author.findUnique({ where: { id } });

    if (author) {
      await redisClient.set(cacheKey, JSON.stringify(author), {
        EX: CACHE_TTL,
      });
    }

    return author || undefined;
  }

  async findByLogin(login: string): Promise<Author | undefined> {
    const cacheKey = `${CACHE_PREFIX.AUTHOR}:login:${login}`;

    const cached = await redisClient.get(cacheKey);
    if (cached) {
      console.log(`[Cache HIT] ${cacheKey}`);
      return JSON.parse(cached);
    }

    console.log(`[Cache MISS] ${cacheKey}`);
    const author = await prisma.author.findUnique({ where: { login } });

    if (author) {
      await redisClient.set(cacheKey, JSON.stringify(author), {
        EX: CACHE_TTL,
      });
    }

    return author || undefined;
  }

  async save(entity: Omit<Author, 'id'>): Promise<Author> {
    const author = await prisma.author.create({ data: entity });

    await this.invalidateListCaches();

    return author;
  }

  async update(
    id: number,
    entity: Partial<Author>
  ): Promise<Author | undefined> {
    try {
      const author = await prisma.author.update({
        where: { id },
        data: entity,
      });

      await redisClient.del(`${CACHE_PREFIX.AUTHOR}:${id}`);
      await this.invalidateListCaches();

      return author;
    } catch {
      return undefined;
    }
  }

  async delete(id: number): Promise<boolean> {
    try {
      await prisma.author.delete({ where: { id } });

      await redisClient.del(`${CACHE_PREFIX.AUTHOR}:${id}`);
      await this.invalidateListCaches();

      return true;
    } catch {
      return false;
    }
  }

  async existsById(id: number): Promise<boolean> {
    const count = await prisma.author.count({ where: { id } });
    return count > 0;
  }

  private async invalidateListCaches(): Promise<void> {
    const keys = await redisClient.keys(`${CACHE_PREFIX.AUTHOR}:all:*`);
    if (keys.length > 0) {
      await redisClient.del(keys);
    }
  }
}
