import {
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { StickerRequestTo } from '../../../dto/stickers/StickerRequestTo.dto';
import { StickerResponseTo } from '../../../dto/stickers/StickerResponseTo.dto';

@Injectable()
export class StickersService {
  constructor(private prisma: PrismaService) {}

  async createSticker(sticker: StickerRequestTo): Promise<StickerResponseTo> {
    return this.prisma.sticker.create({
      data: sticker,
    });
  }

  async getAll(): Promise<StickerResponseTo[]> {
    return this.prisma.sticker.findMany();
  }

  async getSticker(id: number): Promise<StickerResponseTo> {
    const sticker = await this.prisma.sticker.findUnique({
      where: { id },
    });

    if (!sticker) {
      throw new NotFoundException('No sticker found');
    }

    return sticker;
  }

  async updateSticker(
    id: number,
    sticker: StickerRequestTo,
  ): Promise<StickerResponseTo> {
    const existSticker = await this.prisma.sticker.findUnique({
      where: { id },
    });

    if (!existSticker) {
      throw new NotFoundException('No sticker found');
    }

    try {
      return await this.prisma.sticker.update({
        where: { id },
        data: sticker,
      });
    } catch {
      throw new InternalServerErrorException('Database error occurred');
    }
  }

  async deleteSticker(id: number): Promise<void> {
    const existSticker = await this.prisma.sticker.findUnique({
      where: { id },
    });

    if (!existSticker) {
      throw new NotFoundException('No sticker found');
    }

    await this.prisma.sticker.delete({ where: { id } });
  }
}
