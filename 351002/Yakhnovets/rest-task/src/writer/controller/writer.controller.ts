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
import { WriterService } from '../service/writer.service';
import { WriterRequestTo } from '../dto/writer-request.to';
import { WriterResponseTo } from '../dto/writer-response.to';

@Controller('api/v1.0/writers')
export class WriterController {
  constructor(private readonly service: WriterService) {}

  @Get()
  getAll(): Promise<WriterResponseTo[]> {
    return this.service.getAll();
  }

  @Get(':id')
  getById(@Param('id', ParseIntPipe) id: number): Promise<WriterResponseTo> {
    return this.service.getById(id);
  }

  @Post()
  create(@Body() dto: WriterRequestTo): Promise<WriterResponseTo> {
    return this.service.create(dto); // 201 by default
  }

  @Put(':id')
  update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: WriterRequestTo,
  ): Promise<WriterResponseTo> {
    return this.service.update(id, dto);
  }

  @Delete(':id')
  @HttpCode(204)
  delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.service.delete(id);
  }
}
