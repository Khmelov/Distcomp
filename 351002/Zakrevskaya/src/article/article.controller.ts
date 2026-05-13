import { Controller, Get, Post, Put, Delete, Body, Param, Query, HttpCode, HttpStatus, UseGuards, ParseIntPipe } from '@nestjs/common';
import { ArticleService } from './article.service';
import { CreateArticleDto, ArticleResponseDto } from './dto/article.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { RolesGuard } from '../auth/roles.guard';
import { Roles } from '../auth/roles.decorator';

@Controller('api/v1.0/articles')
export class ArticleController {
  constructor(private readonly articleService: ArticleService) {}

  @Post()
  @HttpCode(HttpStatus.CREATED)
  async create(@Body() createArticleDto: CreateArticleDto): Promise<ArticleResponseDto> {
    return this.articleService.create(createArticleDto);
  }

  @Get()
  async findAll(
    @Query('page') page: number = 1,
    @Query('limit') limit: number = 10,
  ): Promise<ArticleResponseDto[]> {
    return this.articleService.findAll(page, limit);
  }

  @Get(':id')
  async findById(@Param('id', ParseIntPipe) id: number): Promise<ArticleResponseDto> {
    return this.articleService.findById(id);
  }

  @Put(':id')
  async update(@Param('id', ParseIntPipe) id: number, @Body() updateArticleDto: CreateArticleDto): Promise<ArticleResponseDto> {
    return this.articleService.update(id, updateArticleDto);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.articleService.delete(id);
  }
}

@Controller('api/v2.0/articles')
export class ArticleV2Controller {
  constructor(private readonly articleService: ArticleService) {}

  @Post()
  @HttpCode(HttpStatus.CREATED)
  @UseGuards(JwtAuthGuard)
  async create(@Body() createArticleDto: CreateArticleDto): Promise<ArticleResponseDto> {
    return this.articleService.create(createArticleDto);
  }

  @Get()
  @UseGuards(JwtAuthGuard)
  async findAll(
    @Query('page') page: number = 1,
    @Query('limit') limit: number = 10,
  ): Promise<ArticleResponseDto[]> {
    return this.articleService.findAll(page, limit);
  }

  @Get(':id')
  @UseGuards(JwtAuthGuard)
  async findById(@Param('id', ParseIntPipe) id: number): Promise<ArticleResponseDto> {
    return this.articleService.findById(id);
  }

  @Put(':id')
  @UseGuards(JwtAuthGuard)
  async update(@Param('id', ParseIntPipe) id: number, @Body() updateArticleDto: CreateArticleDto): Promise<ArticleResponseDto> {
    return this.articleService.update(id, updateArticleDto);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('ADMIN')
  async delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.articleService.delete(id);
  }
}