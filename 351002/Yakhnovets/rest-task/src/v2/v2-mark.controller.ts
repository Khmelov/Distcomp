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
  UseFilters,
  UseGuards,
} from '@nestjs/common';
import { JwtAuthGuard } from '../security/jwt-auth.guard';
import { Roles } from '../security/roles.decorator';
import { RolesGuard } from '../security/roles.guard';
import { WriterRole } from '../security/role.enum';
import { MarkRequestTo } from '../mark/dto/mark-request.to';
import { MarkResponseTo } from '../mark/dto/mark-response.to';
import { MarkService } from '../mark/service/mark.service';
import { V2HttpExceptionFilter } from './v2-http-exception.filter';

@UseFilters(V2HttpExceptionFilter)
@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('api/v2.0/marks')
export class V2MarkController {
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
  @Roles(WriterRole.ADMIN)
  create(@Body() dto: MarkRequestTo): Promise<MarkResponseTo> {
    return this.service.create(dto);
  }

  @Put(':id')
  @Roles(WriterRole.ADMIN)
  update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: MarkRequestTo,
  ): Promise<MarkResponseTo> {
    return this.service.update(id, dto);
  }

  @Delete(':id')
  @HttpCode(204)
  @Roles(WriterRole.ADMIN)
  delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.service.delete(id);
  }
}
