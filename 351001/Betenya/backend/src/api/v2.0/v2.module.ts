import { Module } from '@nestjs/common';
import { UsersModule } from '../v1.0/users/users.module';
import { ArticlesModule } from '../v1.0/articles/articles.module';
import { StickersModule } from '../v1.0/stickers/stickers.module';
import { NoticesModule } from '../v1.0/notices/notices.module';
import { AuthController } from './auth/auth.controller';
import { V2UsersController } from './users/users.controller';
import { V2ArticlesController } from './articles/articles.controller';
import { V2StickersController } from './stickers/stickers.controller';
import { V2NoticesController } from './notices/notices.controller';

@Module({
  imports: [UsersModule, ArticlesModule, StickersModule, NoticesModule],
  controllers: [
    AuthController,
    V2UsersController,
    V2ArticlesController,
    V2StickersController,
    V2NoticesController,
  ],
})
export class V2AppModule {}
