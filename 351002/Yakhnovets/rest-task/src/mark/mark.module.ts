import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { MarkController } from './controller/mark.controller';
import { MarkService } from './service/mark.service';
import { Mark } from './entity/mark.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Mark])],
  controllers: [MarkController],
  providers: [MarkService],
  exports: [MarkService],
})
export class MarkModule {}
