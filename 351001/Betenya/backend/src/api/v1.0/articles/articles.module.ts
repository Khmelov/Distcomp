import { Module } from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { ArticlesService } from './articles.service';
import { ArticlesController } from './articles.controller';

@Module({
  imports: [],
  controllers: [ArticlesController],
  providers: [ArticlesService, PrismaService],
  exports: [ArticlesService],
})
export class ArticlesModule {}
