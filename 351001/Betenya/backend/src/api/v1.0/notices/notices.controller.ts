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
import { NoticesService } from './notices.service';
import { ApiBody, ApiOperation, ApiParam, ApiResponse } from '@nestjs/swagger';
import { NoticeRequestTo } from '../../../dto/notices/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../../../dto/notices/NoticeResponseTo.dto';

@Controller()
export class NoticesController {
  constructor(private readonly noticesService: NoticesService) {}

  @Post()
  @ApiOperation({ summary: 'Create new notice' })
  @ApiBody({ description: 'New notice fields', type: NoticeRequestTo })
  @ApiResponse({
    status: 201,
    description: 'Successfully created notice',
    type: NoticeResponseTo,
  })
  @ApiResponse({
    status: 401,
    description: 'Article not found',
  })
  async createNotice(
    @Body() notice: NoticeRequestTo,
  ): Promise<NoticeResponseTo> {
    return this.noticesService.createNotice(notice);
  }

  @Get()
  @ApiOperation({ summary: 'Get notices list' })
  @ApiResponse({
    status: 200,
    description: 'Notices list',
    type: [NoticeResponseTo],
  })
  async getAllNotices(): Promise<NoticeResponseTo[]> {
    return this.noticesService.getAll();
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get Notice by ID' })
  @ApiParam({ name: 'id', type: Number, description: 'Id of the Notice' })
  @ApiResponse({
    status: 200,
    description: 'Notice data',
    type: NoticeResponseTo,
  })
  @ApiResponse({ status: 404, description: 'Notice not found' })
  async getNotice(@Param('id') id: number): Promise<NoticeResponseTo> {
    return this.noticesService.getNotice(id);
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update Notice' })
  @ApiParam({ name: 'id', type: Number, description: 'Id of the Notice' })
  @ApiBody({ description: 'Updated notice fields', type: NoticeRequestTo })
  @ApiResponse({
    status: 200,
    description: 'Successfully updated notice',
    type: NoticeResponseTo,
  })
  @ApiResponse({ status: 404, description: 'Notice not found' })
  async updateNotice(
    @Param('id', ParseIntPipe) id: number,
    @Body() article: NoticeRequestTo,
  ): Promise<NoticeResponseTo> {
    return this.noticesService.updateNotice(id, article);
  }

  @HttpCode(204)
  @Delete(':id')
  @ApiOperation({ summary: 'Delete Notice' })
  @ApiParam({ name: 'id', type: Number, description: 'Id of the Notice' })
  @ApiResponse({ status: 404, description: 'Notice not found' })
  async deleteNotice(@Param('id') id: number): Promise<void> {
    return this.noticesService.deleteNotice(id);
  }
}
