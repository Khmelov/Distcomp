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
import { Public } from '../security/public.decorator';
import { JwtAuthGuard } from '../security/jwt-auth.guard';
import { Roles } from '../security/roles.decorator';
import { RolesGuard } from '../security/roles.guard';
import { WriterRole } from '../security/role.enum';
import { V2HttpExceptionFilter } from './v2-http-exception.filter';
import { WriterRequestTo } from '../writer/dto/writer-request.to';
import { WriterResponseTo } from '../writer/dto/writer-response.to';
import { WriterService } from '../writer/service/writer.service';
import type { JwtPayload } from '../security/jwt-payload.interface';

@UseFilters(V2HttpExceptionFilter)
@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('api/v2.0/writers')
export class V2WriterController {
  constructor(private readonly service: WriterService) {}

  @Get()
  @Roles(WriterRole.ADMIN)
  getAll(): Promise<WriterResponseTo[]> {
    return this.service.getAll();
  }

  @Get(':id')
  getById(
    @Param('id', ParseIntPipe) id: number,
  ): Promise<WriterResponseTo> {
    return this.service.getById(id);
  }

  @Public()
  @Post()
  create(@Body() dto: WriterRequestTo): Promise<WriterResponseTo> {
    return this.service.createSecure(dto);
  }

  @Put(':id')
  async update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: WriterRequestTo,
    @CurrentUser() user?: JwtPayload,
  ): Promise<WriterResponseTo> {
    await this.ensureAdminOrSelf(id, user);
    return this.service.updateSecure(id, dto);
  }

  @Delete(':id')
  @HttpCode(204)
  @Roles(WriterRole.ADMIN)
  delete(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.service.delete(id);
  }

  private async ensureAdminOrSelf(id: number, user?: JwtPayload): Promise<void> {
    if (user?.role === WriterRole.ADMIN) {
      return;
    }
    if (!user) {
      throw new ForbiddenException('Insufficient permissions');
    }
    const writer = await this.service.getWriterEntityById(id);
    if (writer.login !== user.sub) {
      throw new ForbiddenException('Insufficient permissions');
    }
  }
}
