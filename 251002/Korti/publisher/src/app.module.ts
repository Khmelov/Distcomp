import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { EditorModule } from './providers/Editor/Editor.Module';
import { ArticleModule } from './providers/Article/Article.module';
import { StickerModule } from './providers/Sticker/Sticker.module';
import { MiddlewareConsumer } from '@nestjs/common';
import { JsonHeadersMiddleware } from './middleware/jsonMiddleware';
import { NoteModule } from './providers/Note/Note.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Editor } from './entities/Editor';
import { Article } from './entities/Article';
import { Note } from './entities/Note';
import { Sticker } from './entities/Sticker';
import { config } from 'dotenv';
import { CacheModule } from '@nestjs/cache-manager';
config();
@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'postgres',
      host: process.env.DB_HOST,
      port: Number(process.env.DB_PORT),
      username: process.env.DB_USERNAME,
      password: process.env.DB_PASSWORD,
      database: process.env.DB_NAME,
      entities: [Editor, Article, Note, Sticker],
      synchronize: false,
    }),
    CacheModule.register({
      isGlobal: true,
      ttl: 60,
      max: 100,
    }),
    EditorModule,
    ArticleModule,
    NoteModule,
    StickerModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(JsonHeadersMiddleware).forRoutes('*');
  }
}
