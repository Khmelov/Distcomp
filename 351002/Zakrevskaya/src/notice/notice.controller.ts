import { Controller, Get, Post, Put, Delete, Body, Param, Query, HttpCode, HttpStatus, Req, UseGuards } from '@nestjs/common';
import { Request } from 'express';
import { NoticeService } from './notice.service';
import { CreateNoticeDto } from './dto/notice.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { RolesGuard } from '../auth/roles.guard';
import { Roles } from '../auth/roles.decorator';

@Controller('api/v1.0/notices')
export class NoticeController {
  constructor(private readonly noticeService: NoticeService) {}

  private getPort(req: Request): number {
    return (req.socket as any).localPort || 24110;
  }

  @Post()
  @HttpCode(HttpStatus.CREATED)
  async create(@Body() dto: CreateNoticeDto, @Req() req: Request) {
    return this.noticeService.create(dto, this.getPort(req));
  }

  @Get()
  async findAll(@Req() req: Request, @Query('articleId') articleId?: string) {
    const port = this.getPort(req);
    return this.noticeService.findAll(port, articleId ? parseInt(articleId, 10) : null);
  }

  @Get(':id')
  async findById(
    @Param('id') id: string,
    @Req() req: Request,
    @Query('articleId') aid?: string
  ) {
    const port = this.getPort(req);
    return this.noticeService.findById(parseInt(id, 10), port, aid ? parseInt(aid, 10) : undefined);
  }

  @Put(':id')
  async update(@Param('id') id: string, @Body() dto: CreateNoticeDto, @Req() req: Request) {
    return this.noticeService.update(parseInt(id, 10), dto, this.getPort(req));
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async delete(
    @Param('id') id: string,
    @Req() req: Request,
    @Query('articleId') aid?: string
  ) {
    const port = this.getPort(req);
    await this.noticeService.delete(parseInt(id, 10), port, aid ? parseInt(aid, 10) : undefined);
  }
}

@Controller('api/v2.0/notices')
export class NoticeV2Controller {
  constructor(private readonly noticeService: NoticeService) {}

  private getPort(req: Request): number {
    return (req.socket as any).localPort || 24110;
  }

  @Post()
  @HttpCode(HttpStatus.CREATED)
  @UseGuards(JwtAuthGuard)
  async create(@Body() dto: CreateNoticeDto, @Req() req: Request) {
    return this.noticeService.create(dto, this.getPort(req));
  }

  @Get()
  @UseGuards(JwtAuthGuard)
  async findAll(@Req() req: Request, @Query('articleId') articleId?: string) {
    const port = this.getPort(req);
    return this.noticeService.findAll(port, articleId ? parseInt(articleId, 10) : null);
  }

  @Get(':id')
  @UseGuards(JwtAuthGuard)
  async findById(
    @Param('id') id: string,
    @Req() req: Request,
    @Query('articleId') aid?: string
  ) {
    const port = this.getPort(req);
    return this.noticeService.findById(parseInt(id, 10), port, aid ? parseInt(aid, 10) : undefined);
  }

  @Put(':id')
  @UseGuards(JwtAuthGuard)
  async update(@Param('id') id: string, @Body() dto: CreateNoticeDto, @Req() req: Request) {
    return this.noticeService.update(parseInt(id, 10), dto, this.getPort(req));
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @UseGuards(JwtAuthGuard, RolesGuard)
  @Roles('ADMIN')
  async delete(
    @Param('id') id: string,
    @Req() req: Request,
    @Query('articleId') aid?: string
  ) {
    const port = this.getPort(req);
    await this.noticeService.delete(parseInt(id, 10), port, aid ? parseInt(aid, 10) : undefined);
  }
}