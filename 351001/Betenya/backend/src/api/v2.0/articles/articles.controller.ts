import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpException,
  HttpStatus,
  Param,
  ParseIntPipe,
  Post,
  Put,
  Req,
  UseGuards,
} from '@nestjs/common';
import { ArticlesService } from '../../v1.0/articles/articles.service';
import { JwtAuthGuard } from '../../../auth/jwt-auth.guard';
import { ArticleRequestTo } from '../../../dto/articles/ArticleRequestTo.dto';

@Controller('v2.0/articles')
@UseGuards(JwtAuthGuard)
export class V2ArticlesController {
  constructor(private readonly articlesService: ArticlesService) {}

  @Post()
  async create(@Body() dto: ArticleRequestTo, @Req() req: any) {
    if (req.user.role !== 'ADMIN') {
      if (Number(dto.userId) !== req.user.id) {
        throw new HttpException(
          { errorMessage: 'Can only create articles for yourself', errorCode: 40300 },
          HttpStatus.FORBIDDEN,
        );
      }
    }
    return this.articlesService.createArticle(dto);
  }

  @Get()
  async getAll() {
    return this.articlesService.getAll();
  }

  @Get(':id')
  async getById(@Param('id', ParseIntPipe) id: number) {
    return this.articlesService.getArticleById(id);
  }

  @Put(':id')
  async update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: ArticleRequestTo,
    @Req() req: any,
  ) {
    if (req.user.role !== 'ADMIN') {
      const article = await this.articlesService.getArticleById(id);
      if (Number(article.userId) !== req.user.id) {
        throw new HttpException(
          { errorMessage: 'Access denied', errorCode: 40300 },
          HttpStatus.FORBIDDEN,
        );
      }
    }
    return this.articlesService.updateArticle(id, dto);
  }

  @HttpCode(204)
  @Delete(':id')
  async delete(@Param('id', ParseIntPipe) id: number, @Req() req: any) {
    if (req.user.role !== 'ADMIN') {
      const article = await this.articlesService.getArticleById(id);
      if (Number(article.userId) !== req.user.id) {
        throw new HttpException(
          { errorMessage: 'Access denied', errorCode: 40300 },
          HttpStatus.FORBIDDEN,
        );
      }
    }
    return this.articlesService.deleteArticle(id);
  }
}
