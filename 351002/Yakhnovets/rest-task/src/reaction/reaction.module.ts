import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ReactionController } from './controller/reaction.controller';
import { ReactionCacheService } from './service/reaction-cache.service';
import { ReactionService } from './service/reaction.service';
import { Issue } from '../issue/entity/issue.entity';

const discussionBaseUrl =
  process.env.DISCUSSION_BASE_URL ?? 'http://localhost:24130/api/v1.0';

@Module({
  imports: [
    HttpModule.register({
      baseURL: discussionBaseUrl,
      timeout: 10_000,
    }),
    TypeOrmModule.forFeature([Issue]),
  ],
  controllers: [ReactionController],
  providers: [ReactionService, ReactionCacheService],
  exports: [ReactionService],
})
export class ReactionModule {}
