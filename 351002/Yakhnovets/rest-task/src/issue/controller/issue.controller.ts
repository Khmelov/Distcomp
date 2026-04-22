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
import { IssueService } from '../service/issue.service';
import { IssueRequestTo } from '../dto/issue-request.to';
import { IssueResponseTo } from '../dto/issue-response.to';

@Controller('issues')
export class IssueController {
  constructor(private readonly service: IssueService) {}

  @Get()
  getAll(): Promise<IssueResponseTo[]> {
    return this.service.getAll();
  }

  @Get(':id')
  getById(@Param('id', ParseIntPipe) id: number): Promise<IssueResponseTo> {
    return this.service.getById(id);
  }

  @Post()
  create(@Body() dto: IssueRequestTo): Promise<IssueResponseTo> {
    return this.service.create(dto);
  }

  @Put(':id')
  update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: IssueRequestTo,
  ): Promise<IssueResponseTo> {
    return this.service.update(id, dto);
  }

  @Delete(':id')
  @HttpCode(204)
  delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.service.delete(id);
  }
}
