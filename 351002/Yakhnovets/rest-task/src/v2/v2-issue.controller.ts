import {
  Body,
  Controller,
  Delete,
  ForbiddenException,
  Get,
  HttpCode,
  Param,
  ParseIntPipe,
  Post,
  Put,
  UseFilters,
  UseGuards,
} from '@nestjs/common';
import { CurrentUser } from '../security/current-user.decorator';
import { JwtAuthGuard } from '../security/jwt-auth.guard';
import { RolesGuard } from '../security/roles.guard';
import { WriterRole } from '../security/role.enum';
import { IssueRequestTo } from '../issue/dto/issue-request.to';
import { IssueResponseTo } from '../issue/dto/issue-response.to';
import { IssueService } from '../issue/service/issue.service';
import { WriterService } from '../writer/service/writer.service';
import { V2HttpExceptionFilter } from './v2-http-exception.filter';
import type { JwtPayload } from '../security/jwt-payload.interface';

@UseFilters(V2HttpExceptionFilter)
@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('api/v2.0/issues')
export class V2IssueController {
  constructor(
    private readonly service: IssueService,
    private readonly writerService: WriterService,
  ) {}

  @Get()
  getAll(): Promise<IssueResponseTo[]> {
    return this.service.getAll();
  }

  @Get(':id')
  getById(@Param('id', ParseIntPipe) id: number): Promise<IssueResponseTo> {
    return this.service.getById(id);
  }

  @Post()
  async create(
    @Body() dto: IssueRequestTo,
    @CurrentUser() user?: JwtPayload,
  ): Promise<IssueResponseTo> {
    await this.ensureWriterAccess(dto.writerId, user);
    return this.service.create(dto);
  }

  @Put(':id')
  async update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: IssueRequestTo,
    @CurrentUser() user?: JwtPayload,
  ): Promise<IssueResponseTo> {
    await this.ensureWriterAccess(dto.writerId, user);
    return this.service.update(id, dto);
  }

  @Delete(':id')
  @HttpCode(204)
  async delete(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() user?: JwtPayload,
  ): Promise<void> {
    const issue = await this.service.getById(id);
    await this.ensureWriterAccess(issue.writerId, user);
    return this.service.delete(id);
  }

  private async ensureWriterAccess(writerId: number, user?: JwtPayload): Promise<void> {
    if (user?.role === WriterRole.ADMIN) {
      return;
    }
    if (!user) {
      throw new ForbiddenException('Insufficient permissions');
    }
    const writer = await this.writerService.getWriterEntityById(writerId);
    if (writer.login !== user.sub) {
      throw new ForbiddenException('Insufficient permissions');
    }
  }
}
