import {
  ConflictException,
  HttpException,
  HttpStatus,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { Article } from '../../entities/Article';
import {
  ArticleRequestTo,
  ArticleSearchParamsDto,
  UpdateArticleTo,
} from './Dto/ArticleRequestTo';
import { ArticleResponseTo } from './Dto/ArticleResponseTo';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Editor } from '../../entities/Editor';
import { plainToInstance } from 'class-transformer';

@Injectable()
export class ArticleService {
  constructor(
    @InjectRepository(Article)
    private readonly articleRepository: Repository<Article>,
    @InjectRepository(Editor)
    private readonly editorRepository: Repository<Editor>,
  ) {}

  async getAllArticles(): Promise<ReadonlyArray<ArticleResponseTo>> {
    const articles = await this.articleRepository.find();
    return plainToInstance(ArticleResponseTo, articles, {
      excludeExtraneousValues: true,
    });
  }

  async createArticle(article: ArticleRequestTo): Promise<ArticleResponseTo> {
    try {
      const editor = await this.editorRepository.findOne({
        where: { id: article.editorId },
      });
      if (!editor) {
        throw new NotFoundException();
      }
      const newArticle = this.articleRepository.create({
        ...article,
        editor: editor,
      });
      const res = await this.articleRepository.save(newArticle);
      return plainToInstance(ArticleResponseTo, res, {
        excludeExtraneousValues: true,
      });
    } catch (err) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (err.code === '23505') {
        throw new HttpException(
          {
            errorCode: 40204,
            errorMessage: 'Article already exist.',
          },
          HttpStatus.FORBIDDEN,
        );
      } else if (err instanceof NotFoundException) {
        throw new HttpException(
          {
            errorCode: 40400,
            errorMessage: 'Editor does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      }
      throw new InternalServerErrorException();
    }
  }

  async deleteArticle(id: number): Promise<void> {
    try {
      const article = await this.articleRepository.findOne({ where: { id } });
      if (!article) throw new ConflictException();
      await this.articleRepository.delete(id);
    } catch (err) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      if (err instanceof ConflictException) {
        throw new HttpException(
          {
            errorCode: 40403,
            errorMessage: 'Article does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      }
      throw new InternalServerErrorException();
    }
  }

  async getArticleById(id: number): Promise<ArticleResponseTo> {
    try {
      const article = await this.articleRepository.findOne({ where: { id } });
      if (!article) throw new ConflictException();
      return plainToInstance(ArticleResponseTo, article, {
        excludeExtraneousValues: true,
      });
    } catch (err) {
      if (err instanceof ConflictException) {
        throw new HttpException(
          {
            errorCode: 40403,
            errorMessage: 'Article does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      }
      throw new InternalServerErrorException();
    }
  }

  async updateArticle(body: UpdateArticleTo): Promise<ArticleResponseTo> {
    try {
      const editor = await this.editorRepository.findOne({
        where: { id: body.editorId },
      });
      if (!editor) throw new NotFoundException();
      const article = await this.articleRepository.findOne({
        where: { id: body.id },
      });
      if (!article) throw new ConflictException();
      const modDate = new Date();
      const modArticle: Article = {
        ...body,
        created: article.created,
        notes: article.notes,
        editor: article.editor,
        stickers: article.stickers,
        modified: modDate,
      };
      await this.articleRepository.update(body.id, {
        editorId: modArticle.editorId,
        title: modArticle.title,
        content: modArticle.content,
        editor: modArticle.editor,
        stickers: modArticle.stickers,
        notes: modArticle.notes,
        created: modArticle.created,
        modified: modArticle.modified,
      });
      return plainToInstance(ArticleResponseTo, modArticle, {
        excludeExtraneousValues: true,
      });
    } catch (err) {
      if (err instanceof ConflictException) {
        throw new HttpException(
          {
            errorCode: 40403,
            errorMessage: 'Article does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      } else if (err instanceof NotFoundException) {
        throw new HttpException(
          {
            errorCode: 40400,
            errorMessage: 'Editor does not exist.',
          },
          HttpStatus.NOT_FOUND,
        );
      }
      throw new InternalServerErrorException();
    }
  }

  async findByParams(
    params: ArticleSearchParamsDto,
  ): Promise<ArticleResponseTo[]> {
    const queryBuilder = this.articleRepository
      .createQueryBuilder('article')
      .leftJoinAndSelect('article.editor', 'editor')
      .leftJoinAndSelect('article.stickers', 'stickers');
    if (params.stickerNames) {
      queryBuilder.andWhere('stickers.name IN (:...stickerNames)', {
        stickerNames: params.stickerNames,
      });
    }
    if (params.stickerIds) {
      queryBuilder.andWhere('stickers.id IN (:...stickerIds)', {
        stickerIds: params.stickerIds,
      });
    }
    if (params.editorLogin) {
      queryBuilder.andWhere('editor.login = :editorLogin', {
        editorLogin: params.editorLogin,
      });
    }
    if (params.title) {
      queryBuilder.andWhere('article.title ILIKE :title', {
        title: `%${params.title}%`,
      });
    }
    if (params.content) {
      queryBuilder.andWhere('article.content ILIKE :content', {
        content: `%${params.content}%`,
      });
    }

    const articles = await queryBuilder.getMany();

    return plainToInstance(ArticleResponseTo, articles, {
      excludeExtraneousValues: true,
    });
  }
}
