import { Module } from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { NoticesService } from './notices.service';
import { NoticesController } from './notices.controller';

@Module({
  imports: [],
  controllers: [NoticesController],
  providers: [NoticesService, PrismaService],
  exports: [NoticesService],
})
export class NoticesModule {}
