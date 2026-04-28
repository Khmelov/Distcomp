import { Global, Module } from '@nestjs/common';
import { CassandraService } from './cassandra.service';

@Global()
@Module({
  providers: [CassandraService],
  exports: [CassandraService],
})
export class CassandraModule {}
