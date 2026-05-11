import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ArticleController, ArticleV2Controller } from './article.controller';
import { ArticleService } from './article.service';
import { ArticleRepository } from './article.repository';
import { Article } from './article.entity';
import { UserModule } from '../user/user.module';
import { MarkerModule } from '../marker/marker.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([Article]),
    UserModule,
    MarkerModule,
  ],
  controllers: [ArticleController, ArticleV2Controller],
  providers: [ArticleService, ArticleRepository],
  exports: [ArticleRepository],
})
export class ArticleModule {}