import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  Param,
  ParseIntPipe,
  Post,
  Put,
} from '@nestjs/common';
import { ArticlesService } from './articles.service';
import { ApiBody, ApiOperation, ApiParam, ApiResponse } from '@nestjs/swagger';
import { ArticleRequestTo } from '../../../dto/articles/ArticleRequestTo.dto';
import { ArticleResponseTo } from '../../../dto/articles/ArticleResponseTo.dto';

@Controller()
export class ArticlesController {
  constructor(private readonly articlesService: ArticlesService) {}

  @Post()
  @ApiOperation({ summary: 'Create new article' })
  @ApiBody({ description: 'New article fields', type: ArticleRequestTo })
  @ApiResponse({
    status: 201,
    description: 'Article created successfully.',
    type: ArticleResponseTo,
  })
  @ApiResponse({
    status: 401,
    description: 'User with userId not found.',
  })
  async createArticle(
    @Body() article: ArticleRequestTo,
  ): Promise<ArticleResponseTo> {
    return this.articlesService.createArticle(article);
  }

  @Get()
  @ApiOperation({ summary: 'Get articles list' })
  @ApiResponse({
    status: 200,
    description: 'Articles list',
    type: [ArticleResponseTo],
  })
  async getAllArticles(): Promise<ArticleResponseTo[]> {
    return this.articlesService.getAll();
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get article by id' })
  @ApiParam({ name: 'id', type: Number, description: 'The id of the article' })
  @ApiResponse({
    status: 200,
    description: 'Article found with id of the article',
    type: ArticleResponseTo,
  })
  @ApiResponse({ status: 404, description: 'Article not found.' })
  async getArticle(@Param('id') id: number): Promise<ArticleResponseTo> {
    return this.articlesService.getArticleById(id);
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update article' })
  @ApiParam({ name: 'id', type: Number, description: 'The id of the article' })
  @ApiBody({ description: 'Update article fields', type: ArticleRequestTo })
  @ApiResponse({
    status: 200,
    description: 'Article updated successfully.',
    type: ArticleResponseTo,
  })
  @ApiResponse({ status: 404, description: 'Article not found.' })
  async updateArticle(
    @Param('id', ParseIntPipe) id: number,
    @Body() article: ArticleRequestTo,
  ): Promise<ArticleResponseTo> {
    return this.articlesService.updateArticle(id, article);
  }

  @HttpCode(204)
  @Delete(':id')
  @ApiOperation({ summary: 'Delete article' })
  @ApiParam({ name: 'id', type: Number, description: 'The id of the article' })
  @ApiResponse({ status: 404, description: 'Article not found.' })
  async deleteArticle(@Param('id') id: number): Promise<void> {
    return this.articlesService.deleteArticle(id);
  }
}
