import { CassandraService } from './cassandra.service';
import { Module } from '@nestjs/common';

@Module({
  providers: [CassandraService],
  exports: [CassandraService],
})
export class CassandraModule {}
