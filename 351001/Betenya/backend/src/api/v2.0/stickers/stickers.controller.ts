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
import { StickersService } from '../../v1.0/stickers/stickers.service';
import { JwtAuthGuard } from '../../../auth/jwt-auth.guard';
import { RolesGuard } from '../../../auth/roles.guard';
import { Roles } from '../../../auth/roles.decorator';
import { StickerRequestTo } from '../../../dto/stickers/StickerRequestTo.dto';

@Controller('v2.0/stickers')
@UseGuards(JwtAuthGuard)
export class V2StickersController {
  constructor(private readonly stickersService: StickersService) {}

  @Post()
  @UseGuards(RolesGuard)
  @Roles('ADMIN')
  async create(@Body() dto: StickerRequestTo) {
    return this.stickersService.createSticker(dto);
  }

  @Get()
  async getAll() {
    return this.stickersService.getAll();
  }

  @Get(':id')
  async getById(@Param('id', ParseIntPipe) id: number) {
    return this.stickersService.getSticker(id);
  }

  @Put(':id')
  @UseGuards(RolesGuard)
  @Roles('ADMIN')
  async update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: StickerRequestTo,
  ) {
    return this.stickersService.updateSticker(id, dto);
  }

  @HttpCode(204)
  @Delete(':id')
  @UseGuards(RolesGuard)
  @Roles('ADMIN')
  async delete(@Param('id', ParseIntPipe) id: number) {
    return this.stickersService.deleteSticker(id);
  }
}
