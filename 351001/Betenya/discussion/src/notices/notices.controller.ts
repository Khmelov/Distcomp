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
import { ApiBody, ApiOperation, ApiParam, ApiResponse, ApiTags } from '@nestjs/swagger';
import { NoticesService } from './notices.service';
import { NoticeRequestTo } from '../dto/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../dto/NoticeResponseTo.dto';

@ApiTags('notices')
@Controller()
export class NoticesController {
  constructor(private readonly noticesService: NoticesService) {}

  @Post()
  @ApiOperation({ summary: 'Create new notice' })
  @ApiBody({ type: NoticeRequestTo })
  @ApiResponse({ status: 201, type: NoticeResponseTo })
  async createNotice(@Body() dto: NoticeRequestTo): Promise<NoticeResponseTo> {
    return this.noticesService.createNotice(dto);
  }

  @Get()
  @ApiOperation({ summary: 'Get all notices' })
  @ApiResponse({ status: 200, type: [NoticeResponseTo] })
  async getAllNotices(): Promise<NoticeResponseTo[]> {
    return this.noticesService.getAll();
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get notice by ID' })
  @ApiParam({ name: 'id', type: Number })
  @ApiResponse({ status: 200, type: NoticeResponseTo })
  @ApiResponse({ status: 404, description: 'Notice not found' })
  async getNotice(@Param('id', ParseIntPipe) id: number): Promise<NoticeResponseTo> {
    return this.noticesService.getNotice(id);
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update notice' })
  @ApiParam({ name: 'id', type: Number })
  @ApiBody({ type: NoticeRequestTo })
  @ApiResponse({ status: 200, type: NoticeResponseTo })
  @ApiResponse({ status: 404, description: 'Notice not found' })
  async updateNotice(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: NoticeRequestTo,
  ): Promise<NoticeResponseTo> {
    return this.noticesService.updateNotice(id, dto);
  }

  @HttpCode(204)
  @Delete(':id')
  @ApiOperation({ summary: 'Delete notice' })
  @ApiParam({ name: 'id', type: Number })
  @ApiResponse({ status: 204 })
  @ApiResponse({ status: 404, description: 'Notice not found' })
  async deleteNotice(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.noticesService.deleteNotice(id);
  }
}
