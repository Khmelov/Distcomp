import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { UserModule } from './user/user.module';
import { ArticleModule } from './article/article.module';
import { MarkerModule } from './marker/marker.module';
import { NoticeModule } from './notice/notice.module';
import { AuthModule } from './auth/auth.module';
import { User } from './user/user.entity';
import { Article } from './article/article.entity';
import { Marker } from './marker/marker.entity';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'postgres',
      host: 'localhost',
      port: 5432,
      username: 'postgres',
      password: 'postgres',
      database: 'distcomp',
      entities: [User, Article, Marker],
      synchronize: true,
      retryAttempts: 5,
      retryDelay: 3000,
    }),
    UserModule,
    ArticleModule,
    MarkerModule,
    NoticeModule,
    AuthModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}