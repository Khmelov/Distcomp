import {
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { NoticeRequestTo } from '../../../dto/notices/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../../../dto/notices/NoticeResponseTo.dto';

@Injectable()
export class NoticesService {
  constructor(private prisma: PrismaService) {}

  async createNotice(notice: NoticeRequestTo): Promise<NoticeResponseTo> {
    const article = await this.prisma.article.findUnique({
      where: { id: notice.articleId },
    });

    if (!article) {
      throw new NotFoundException('Article not found');
    }

    return this.prisma.notice.create({
      data: notice,
    });
  }

  async getAll(): Promise<NoticeResponseTo[]> {
    return this.prisma.notice.findMany();
  }

  async getNotice(id: number): Promise<NoticeResponseTo> {
    const notice = await this.prisma.notice.findUnique({
      where: { id },
    });

    if (!notice) {
      throw new NotFoundException('Notice not found');
    }

    return notice;
  }

  async updateNotice(
    id: number,
    notice: NoticeRequestTo,
  ): Promise<NoticeResponseTo> {
    const existNotice = await this.prisma.notice.findUnique({
      where: { id },
    });

    if (!existNotice) {
      throw new NotFoundException('Notice not found');
    }

    const article = await this.prisma.article.findUnique({
      where: { id: notice.articleId },
    });

    if (!article) {
      throw new NotFoundException('Article not found');
    }

    try {
      return await this.prisma.notice.update({
        where: { id },
        data: notice,
      });
    } catch {
      throw new InternalServerErrorException('Database error occurred');
    }
  }

  async deleteNotice(id: number): Promise<void> {
    const existNotice = await this.prisma.notice.findUnique({
      where: { id },
    });

    if (!existNotice) {
      throw new NotFoundException('Notice not found');
    }

    await this.prisma.notice.delete({ where: { id } });
  }
}
