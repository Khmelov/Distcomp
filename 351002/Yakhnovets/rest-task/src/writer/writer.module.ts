import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { WriterController } from './controller/writer.controller';
import { WriterService } from './service/writer.service';
import { Writer } from './entity/writer.entity';
import { ReactionModule } from '../reaction/reaction.module';

@Module({
  imports: [TypeOrmModule.forFeature([Writer]), ReactionModule],
  controllers: [WriterController],
  providers: [WriterService],
  exports: [WriterService],
})
export class WriterModule {}
