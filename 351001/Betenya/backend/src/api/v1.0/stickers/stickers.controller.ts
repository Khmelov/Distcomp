import { Body, Controller, Delete, Get, HttpCode, Param, ParseIntPipe, Post, Put } from '@nestjs/common';
import { StickersService } from './stickers.service';
import { ApiBody, ApiOperation, ApiParam, ApiResponse } from '@nestjs/swagger';
import { StickerRequestTo } from '../../../dto/stickers/StickerRequestTo.dto';
import { StickerResponseTo } from '../../../dto/stickers/StickerResponseTo.dto';

@Controller()
export class StickersController {
  constructor(private readonly stickersService: StickersService) {}

  @Post()
  @ApiOperation({ summary: 'Create new sticker' })
  @ApiBody({ description: 'New sticker fields', type: StickerRequestTo })
  @ApiResponse({
    status: 201,
    description: 'Successfully created sticker',
    type: StickerResponseTo,
  })
  async createSticker(
    @Body() sticker: StickerRequestTo,
  ): Promise<StickerResponseTo> {
    return this.stickersService.createSticker(sticker);
  }

  @Get()
  @ApiOperation({ summary: 'Get stickers list' })
  @ApiResponse({
    status: 200,
    description: 'Successfully found stickers',
    type: [StickerResponseTo],
  })
  async getAllStickers(): Promise<StickerResponseTo[]> {
    return this.stickersService.getAll();
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get sticker by id' })
  @ApiParam({ name: 'id', type: Number, description: 'Id of the Sticker' })
  @ApiResponse({
    status: 200,
    description: 'Successfully found sticker by id',
    type: StickerResponseTo,
  })
  @ApiResponse({ status: 404, description: 'Sticker not found' })
  async getStickerById(@Param('id') id: number): Promise<StickerResponseTo> {
    return this.stickersService.getSticker(id);
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update sticker' })
  @ApiParam({ name: 'id', type: Number, description: 'Id of the Sticker' })
  @ApiBody({ description: 'New sticker fields', type: StickerRequestTo })
  @ApiResponse({
    status: 200,
    description: 'Successfully updated sticker',
    type: StickerResponseTo,
  })
  @ApiResponse({ status: 404, description: 'Sticker not found' })
  async updateSticker(
    @Param('id', ParseIntPipe) id: number,
    @Body() sticker: StickerRequestTo,
  ): Promise<StickerResponseTo> {
    return this.stickersService.updateSticker(id, sticker);
  }

  @HttpCode(204)
  @Delete(':id')
  @ApiOperation({ summary: 'Delete sticker' })
  @ApiParam({ name: 'id', type: Number, description: 'Id of the Sticker' })
  @ApiResponse({ status: 404, description: 'Sticker not found' })
  async deleteSticker(@Param('id') id: number): Promise<void> {
    return this.stickersService.deleteSticker(id);
  }
}
