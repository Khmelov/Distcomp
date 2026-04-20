import { Module } from '@nestjs/common';
import { KafkaService } from './kafka.service';
import { NoticesModule } from '../notices/notices.module';

@Module({
  imports: [NoticesModule],
  providers: [KafkaService],
  exports: [KafkaService],
})
export class KafkaModule {}
