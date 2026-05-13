import { Module } from '@nestjs/common';
import { CassandraModule } from './cassandra/cassandra.module';
import { ReactionModule } from './reaction/reaction.module';

@Module({
  imports: [CassandraModule, ReactionModule],
})
export class AppModule {}
