import { Module } from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { StickersService } from './stickers.service';
import { StickersController } from './stickers.controller';

@Module({
  imports: [],
  controllers: [StickersController],
  providers: [StickersService, PrismaService],
  exports: [StickersService],
})
export class StickersModule {}
