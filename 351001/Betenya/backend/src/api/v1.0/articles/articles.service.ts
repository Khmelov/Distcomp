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

@Injectable()
export class ArticlesService {
  constructor(private prisma: PrismaService) {}

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

    return this.prisma.article.create({
      data: article,
    });
  }

  async getAll(): Promise<ArticleResponseTo[]> {
    return this.prisma.article.findMany();
  }

  async getArticleById(id: number): Promise<ArticleResponseTo> {
    const article = await this.prisma.article.findUnique({
      where: { id },
    });

    if (!article) {
      throw new UnauthorizedException('Article with id not found!');
    }

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
      return await this.prisma.article.update({
        where: { id },
        data: article,
      });
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

    await this.prisma.article.delete({ where: { id } });
  }
}
