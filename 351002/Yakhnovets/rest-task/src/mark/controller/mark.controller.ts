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
import { MarkService } from '../service/mark.service';
import { MarkRequestTo } from '../dto/mark-request.to';
import { MarkResponseTo } from '../dto/mark-response.to';

@Controller('marks')
export class MarkController {
  constructor(private readonly service: MarkService) {}

  @Get()
  getAll(): Promise<MarkResponseTo[]> {
    return this.service.getAll();
  }

  @Get(':id')
  getById(@Param('id', ParseIntPipe) id: number): Promise<MarkResponseTo> {
    return this.service.getById(id);
  }

  @Post()
  create(@Body() dto: MarkRequestTo): Promise<MarkResponseTo> {
    return this.service.create(dto);
  }

  @Put(':id')
  update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: MarkRequestTo,
  ): Promise<MarkResponseTo> {
    return this.service.update(id, dto);
  }

  @Delete(':id')
  @HttpCode(204)
  delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.service.delete(id);
  }
}
