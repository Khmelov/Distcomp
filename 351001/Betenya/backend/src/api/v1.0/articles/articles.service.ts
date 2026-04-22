import {
  ForbiddenException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { ArticleRequestTo } from '../../../dto/articles/ArticleRequestTo.dto';
import { ArticleResponseTo } from '../../../dto/articles/ArticleResponseTo.dto';
import { RedisService } from '../../../redis/redis.service';
import { Sticker } from '../../../../generated/prisma/client';

const CACHE_PREFIX = 'article';
const CACHE_TTL = 60;

@Injectable()
export class ArticlesService {
  constructor(
    private prisma: PrismaService,
    private redis: RedisService,
  ) {}

  async createArticle(article: ArticleRequestTo): Promise<ArticleResponseTo> {
    const user = await this.prisma.user.findUnique({
      where: { id: article.userId },
    });

    if (!user) {
      throw new UnauthorizedException('User with userId not found!');
    }

    const articleWithTitle = await this.prisma.article.findUnique({
      where: { title: article.title },
    });

    if (articleWithTitle) {
      throw new ForbiddenException('Article with title already exists!');
    }

    const { stickers, ...articleData } = article;

    const created = await this.prisma.$transaction(async (tx) => {
      const stickerRecords: Sticker[] = [];
      if (stickers) {
        for (const sticker of stickers) {
          const find_sticker = await tx.sticker.findFirst({
            where: { name: sticker },
          });

          if (!find_sticker) {
            const newSticker = await tx.sticker.create({
              data: { name: sticker },
            });
            stickerRecords.push(newSticker);
          } else {
            stickerRecords.push(find_sticker);
          }
        }
      }

      const newArticle = await tx.article.create({
        data: articleData,
      });

      if (stickerRecords.length > 0) {
        await tx.articleSticker.createMany({
          data: stickerRecords.map((sticker) => ({
            articleId: newArticle.id,
            stickerId: sticker.id,
          })),
        });
      }

      return newArticle;
    });

    await this.redis.del(`${CACHE_PREFIX}:all`);

    return created;
  }

  async getAll(): Promise<ArticleResponseTo[]> {
    const cached = await this.redis.get<ArticleResponseTo[]>(`${CACHE_PREFIX}:all`);
    if (cached) return cached;

    const articles = await this.prisma.article.findMany();
    await this.redis.set(`${CACHE_PREFIX}:all`, articles, CACHE_TTL);
    return articles;
  }

  async getArticleById(id: number): Promise<ArticleResponseTo> {
    const cached = await this.redis.get<ArticleResponseTo>(`${CACHE_PREFIX}:${id}`);
    if (cached) return cached;

    const article = await this.prisma.article.findUnique({
      where: { id },
    });

    if (!article) {
      throw new UnauthorizedException('Article with id not found!');
    }

    await this.redis.set(`${CACHE_PREFIX}:${id}`, article, CACHE_TTL);
    return article;
  }

  async updateArticle(
    id: number,
    article: ArticleRequestTo,
  ): Promise<ArticleResponseTo> {
    const existArticle = await this.prisma.article.findUnique({
      where: { id },
    });

    if (!existArticle) {
      throw new UnauthorizedException('Article with id not found!');
    }

    try {
      const updated = await this.prisma.article.update({
        where: { id },
        data: article,
      });

      await this.redis.del(`${CACHE_PREFIX}:${id}`, `${CACHE_PREFIX}:all`);

      return updated;
    } catch {
      throw new InternalServerErrorException('Database error occurred');
    }
  }

  async deleteArticle(id: number): Promise<void> {
    const existArticle = await this.prisma.article.findUnique({
      where: { id },
    });

    if (!existArticle) {
      throw new NotFoundException('Article not found');
    }

    await this.prisma.$transaction(async (tx) => {
      const articleStickers = await tx.articleSticker.findMany({
        where: { articleId: id },
        include: {
          sticker: true,
        },
      });

      const stickerIds = articleStickers.map((as) => as.stickerId);

      await tx.articleSticker.deleteMany({
        where: { articleId: id },
      });

      for (const stickerId of stickerIds) {
        const otherConnections = await tx.articleSticker.count({
          where: {
            stickerId: stickerId,
            NOT: { articleId: id },
          },
        });

        if (otherConnections === 0) {
          await tx.sticker.delete({
            where: { id: stickerId },
          });
        }
      }

      await tx.article.delete({ where: { id } });
    });

    await this.redis.del(`${CACHE_PREFIX}:${id}`, `${CACHE_PREFIX}:all`);
  }
}
