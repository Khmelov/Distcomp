import {
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { KafkaService } from '../../../kafka/kafka.service';
import { NoticeRequestTo } from '../../../dto/notices/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../../../dto/notices/NoticeResponseTo.dto';

const DISCUSSION_URL = process.env.DISCUSSION_URL ?? 'http://localhost:24130';
const NOTICES_API = `${DISCUSSION_URL}/api/v1.0/notices`;

@Injectable()
export class NoticesService {
  constructor(
    private prisma: PrismaService,
    private kafkaService: KafkaService,
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
    if (this.kafkaService.isReady()) {
      const raw = await this.kafkaService.sendAndWait(
        'CREATE',
        this.serializeDto(notice),
        String(Number(notice.articleId)), // key = articleId for partition affinity
      );
      return this.toResponseTo(raw as Record<string, unknown>);
    }

    // Fallback to HTTP
    const res = await fetch(NOTICES_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(this.serializeDto(notice)),
    });

    if (!res.ok) {
      throw new InternalServerErrorException('Discussion service error on create');
    }

    return this.toResponseTo((await res.json()) as Record<string, unknown>);
  }

  async getAll(): Promise<NoticeResponseTo[]> {
    const res = await fetch(NOTICES_API);
    if (!res.ok) {
      throw new InternalServerErrorException('Discussion service error on getAll');
    }
    const list = (await res.json()) as Record<string, unknown>[];
    return list.map((raw) => this.toResponseTo(raw));
  }

  async getNotice(id: number): Promise<NoticeResponseTo> {
    const res = await fetch(`${NOTICES_API}/${id}`);
    if (res.status === 404) {
      throw new NotFoundException('Notice not found');
    }
    if (!res.ok) {
      throw new InternalServerErrorException('Discussion service error on get');
    }
    return this.toResponseTo((await res.json()) as Record<string, unknown>);
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
    return this.toResponseTo((await res.json()) as Record<string, unknown>);
  }

  async deleteNotice(id: number): Promise<void> {
    const res = await fetch(`${NOTICES_API}/${id}`, { method: 'DELETE' });
    if (res.status === 404) {
      throw new NotFoundException('Notice not found');
    }
    if (!res.ok) {
      throw new InternalServerErrorException('Discussion service error on delete');
    }
  }
}
