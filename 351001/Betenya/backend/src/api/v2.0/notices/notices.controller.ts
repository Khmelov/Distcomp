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
  UseGuards,
} from '@nestjs/common';
import { NoticesService } from '../../v1.0/notices/notices.service';
import { JwtAuthGuard } from '../../../auth/jwt-auth.guard';
import { NoticeRequestTo } from '../../../dto/notices/NoticeRequestTo.dto';

@Controller('v2.0/notices')
@UseGuards(JwtAuthGuard)
export class V2NoticesController {
  constructor(private readonly noticesService: NoticesService) {}

  @Post()
  async create(@Body() dto: NoticeRequestTo) {
    return this.noticesService.createNotice(dto);
  }

  @Get()
  async getAll() {
    return this.noticesService.getAll();
  }

  @Get(':id')
  async getById(@Param('id', ParseIntPipe) id: number) {
    return this.noticesService.getNotice(id);
  }

  @Put(':id')
  async update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: NoticeRequestTo,
  ) {
    return this.noticesService.updateNotice(id, dto);
  }

  @HttpCode(204)
  @Delete(':id')
  async delete(@Param('id', ParseIntPipe) id: number) {
    return this.noticesService.deleteNotice(id);
  }
}
