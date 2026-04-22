import {
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { StickerRequestTo } from '../../../dto/stickers/StickerRequestTo.dto';
import { StickerResponseTo } from '../../../dto/stickers/StickerResponseTo.dto';
import { RedisService } from '../../../redis/redis.service';

const CACHE_PREFIX = 'sticker';
const CACHE_TTL = 60;

@Injectable()
export class StickersService {
  constructor(
    private prisma: PrismaService,
    private redis: RedisService,
  ) {}

  async createSticker(sticker: StickerRequestTo): Promise<StickerResponseTo> {
    const created = await this.prisma.sticker.create({
      data: sticker,
    });

    await this.redis.del(`${CACHE_PREFIX}:all`);

    return created;
  }

  async getAll(): Promise<StickerResponseTo[]> {
    const cached = await this.redis.get<StickerResponseTo[]>(`${CACHE_PREFIX}:all`);
    if (cached) return cached;

    const stickers = await this.prisma.sticker.findMany();
    await this.redis.set(`${CACHE_PREFIX}:all`, stickers, CACHE_TTL);
    return stickers;
  }

  async getSticker(id: number): Promise<StickerResponseTo> {
    const cached = await this.redis.get<StickerResponseTo>(`${CACHE_PREFIX}:${id}`);
    if (cached) return cached;

    const sticker = await this.prisma.sticker.findUnique({
      where: { id },
    });

    if (!sticker) {
      throw new NotFoundException('No sticker found');
    }

    await this.redis.set(`${CACHE_PREFIX}:${id}`, sticker, CACHE_TTL);
    return sticker;
  }

  async updateSticker(
    id: number,
    sticker: StickerRequestTo,
  ): Promise<StickerResponseTo> {
    const existSticker = await this.prisma.sticker.findUnique({
      where: { id },
    });

    if (!existSticker) {
      throw new NotFoundException('No sticker found');
    }

    try {
      const updated = await this.prisma.sticker.update({
        where: { id },
        data: sticker,
      });

      await this.redis.del(`${CACHE_PREFIX}:${id}`, `${CACHE_PREFIX}:all`);

      return updated;
    } catch {
      throw new InternalServerErrorException('Database error occurred');
    }
  }

  async deleteSticker(id: number): Promise<void> {
    const existSticker = await this.prisma.sticker.findUnique({
      where: { id },
    });

    if (!existSticker) {
      throw new NotFoundException('No sticker found');
    }

    await this.prisma.sticker.delete({ where: { id } });

    await this.redis.del(`${CACHE_PREFIX}:${id}`, `${CACHE_PREFIX}:all`);
  }
}
