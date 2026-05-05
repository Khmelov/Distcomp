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
import { ReactionRequestTo } from './dto/reaction-request.to';
import { ReactionResponseTo } from './dto/reaction-response.to';
import { ReactionService } from './reaction.service';

@Controller('reactions')
export class ReactionController {
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

  @Delete('by-issue/:issueId')
  @HttpCode(204)
  deleteByIssue(@Param('issueId', ParseIntPipe) issueId: number): Promise<void> {
    return this.service.deleteByIssueId(issueId);
  }

  @Delete(':id')
  @HttpCode(204)
  delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.service.delete(id);
  }
}
