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
import { RolesGuard } from '../security/roles.guard';
import { ReactionRequestTo } from '../reaction/dto/reaction-request.to';
import { ReactionResponseTo } from '../reaction/dto/reaction-response.to';
import { ReactionService } from '../reaction/service/reaction.service';
import { V2HttpExceptionFilter } from './v2-http-exception.filter';

@UseFilters(V2HttpExceptionFilter)
@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('api/v2.0/reactions')
export class V2ReactionController {
  constructor(private readonly service: ReactionService) {}

  @Get()
  getAll(): Promise<ReactionResponseTo[]> {
    return this.service.getAll();
  }

  @Get(':id')
  getById(@Param('id', ParseIntPipe) id: number): Promise<ReactionResponseTo> {
    return this.service.getById(id);
  }

  @Post()
  create(@Body() dto: ReactionRequestTo): Promise<ReactionResponseTo> {
    return this.service.create(dto);
  }

  @Put(':id')
  update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: ReactionRequestTo,
  ): Promise<ReactionResponseTo> {
    return this.service.update(id, dto);
  }

  @Delete(':id')
  @HttpCode(204)
  delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.service.delete(id);
  }
}
