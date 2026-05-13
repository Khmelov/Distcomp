import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { MarkerController, MarkerV2Controller } from './marker.controller';
import { MarkerService } from './marker.service';
import { MarkerRepository } from './marker.repository';
import { Marker } from './marker.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Marker])],
  controllers: [MarkerController, MarkerV2Controller],
  providers: [MarkerService, MarkerRepository],
  exports: [MarkerRepository],
})
export class MarkerModule {}