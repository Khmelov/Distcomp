import { MiddlewareConsumer, Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { ConfigModule } from '@nestjs/config';
import { NoteModule } from './providers/Note/Note.module';
import { CassandraModule } from './cassandra/cassandra.module';
import { JsonHeadersMiddleware } from './middleware/jsonMiddleware';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
    }),
    NoteModule,
    CassandraModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(JsonHeadersMiddleware).forRoutes('*');
  }
}
