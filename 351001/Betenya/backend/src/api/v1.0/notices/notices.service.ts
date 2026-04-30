import {
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { KafkaService } from '../../../kafka/kafka.service';
import { NoticeRequestTo } from '../../../dto/notices/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../../../dto/notices/NoticeResponseTo.dto';
import { RedisService } from '../../../redis/redis.service';

const DISCUSSION_URL = process.env.DISCUSSION_URL ?? 'http://localhost:24130';
const NOTICES_API = `${DISCUSSION_URL}/api/v1.0/notices`;

const CACHE_PREFIX = 'notice';
const CACHE_TTL = 60;

@Injectable()
export class NoticesService {
  constructor(
    private prisma: PrismaService,
    private kafkaService: KafkaService,
    private redis: RedisService,
  ) {}

  /** Serialize a DTO to a plain JSON-safe object (BigInt → Number) */
  private serializeDto(dto: NoticeRequestTo): Record<string, unknown> {
    return {
      content: dto.content,
      articleId: Number(dto.articleId),
    };
  }

  /** Map the raw JSON response from discussion service to our DTO shape */
  private toResponseTo(raw: Record<string, unknown>): NoticeResponseTo {
    return {
      id: BigInt(raw.id as number),
      content: raw.content as string,
      articleId: BigInt(raw.articleId as number),
      state: (raw.state as string) ?? 'PENDING',
    };
  }

  async createNotice(notice: NoticeRequestTo): Promise<NoticeResponseTo> {
    // Validate the referenced article exists in the publisher database
    const article = await this.prisma.article.findUnique({
      where: { id: notice.articleId },
    });
    if (!article) {
      throw new NotFoundException('Article not found');
    }

    // Use Kafka transport if available, otherwise fall back to HTTP
    let result: NoticeResponseTo;
    if (this.kafkaService.isReady()) {
      const raw = await this.kafkaService.sendAndWait(
        'CREATE',
        this.serializeDto(notice),
        String(Number(notice.articleId)), // key = articleId for partition affinity
      );
      result = this.toResponseTo(raw as Record<string, unknown>);
    } else {
      // Fallback to HTTP
      const res = await fetch(NOTICES_API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.serializeDto(notice)),
      });

      if (!res.ok) {
        throw new InternalServerErrorException('Discussion service error on create');
      }

      result = this.toResponseTo((await res.json()) as Record<string, unknown>);
    }

    await this.redis.del(`${CACHE_PREFIX}:all`);

    return result;
  }

  async getAll(): Promise<NoticeResponseTo[]> {
    const cached = await this.redis.get<NoticeResponseTo[]>(`${CACHE_PREFIX}:all`);
    if (cached) return cached.map((r) => this.toResponseTo(r as unknown as Record<string, unknown>));

    const res = await fetch(NOTICES_API);
    if (!res.ok) {
      throw new InternalServerErrorException('Discussion service error on getAll');
    }
    const list = (await res.json()) as Record<string, unknown>[];
    const notices = list.map((raw) => this.toResponseTo(raw));

    await this.redis.set(`${CACHE_PREFIX}:all`, list, CACHE_TTL);

    return notices;
  }

  async getNotice(id: number): Promise<NoticeResponseTo> {
    const cached = await this.redis.get<Record<string, unknown>>(`${CACHE_PREFIX}:${id}`);
    if (cached) return this.toResponseTo(cached);

    const res = await fetch(`${NOTICES_API}/${id}`);
    if (res.status === 404) {
      throw new NotFoundException('Notice not found');
    }
    if (!res.ok) {
      throw new InternalServerErrorException('Discussion service error on get');
    }

    const raw = (await res.json()) as Record<string, unknown>;
    await this.redis.set(`${CACHE_PREFIX}:${id}`, raw, CACHE_TTL);

    return this.toResponseTo(raw);
  }

  async updateNotice(id: number, notice: NoticeRequestTo): Promise<NoticeResponseTo> {
    // Validate the referenced article exists in the publisher database
    const article = await this.prisma.article.findUnique({
      where: { id: notice.articleId },
    });
    if (!article) {
      throw new NotFoundException('Article not found');
    }

    const res = await fetch(`${NOTICES_API}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(this.serializeDto(notice)),
    });

    if (res.status === 404) {
      throw new NotFoundException('Notice not found');
    }
    if (!res.ok) {
      throw new InternalServerErrorException('Discussion service error on update');
    }

    const result = this.toResponseTo((await res.json()) as Record<string, unknown>);

    await this.redis.del(`${CACHE_PREFIX}:${id}`, `${CACHE_PREFIX}:all`);

    return result;
  }

  async deleteNotice(id: number): Promise<void> {
    const res = await fetch(`${NOTICES_API}/${id}`, { method: 'DELETE' });
    if (res.status === 404) {
      throw new NotFoundException('Notice not found');
    }
    if (!res.ok) {
      throw new InternalServerErrorException('Discussion service error on delete');
    }

    await this.redis.del(`${CACHE_PREFIX}:${id}`, `${CACHE_PREFIX}:all`);
  }
}
