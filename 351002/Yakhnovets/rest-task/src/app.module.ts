import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { WriterModule } from './writer/writer.module';
import { IssueModule } from './issue/issue.module';
import { MarkModule } from './mark/mark.module';
import { SecurityModule } from './security/security.module';
import { V2Module } from './v2/v2.module';
@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'postgres',
      host: 'localhost',
      port: 5432,
      username: 'postgres',
      password: 'postgres',
      database: 'distcomp',
      autoLoadEntities: true,
      dropSchema: true,
      synchronize: true, // для быстрого старта; позже заменить на миграции
    }),
    WriterModule,
    IssueModule,
    MarkModule,
    SecurityModule,
    V2Module,
  ],
})
export class AppModule {}