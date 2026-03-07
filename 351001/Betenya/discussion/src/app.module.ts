import { Module } from '@nestjs/common';
import { RouterModule } from '@nestjs/core';
import { CassandraModule } from './cassandra/cassandra.module';
import { NoticesModule } from './notices/notices.module';

@Module({
  imports: [
    CassandraModule,
    NoticesModule,
    RouterModule.register([
      {
        path: 'v1.0/notices',
        module: NoticesModule,
      },
    ]),
  ],
})
export class AppModule {}
