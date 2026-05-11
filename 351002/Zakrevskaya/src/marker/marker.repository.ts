import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { BaseRepository } from '../common/repositories/base.repository';
import { Marker } from './marker.entity';

@Injectable()
export class MarkerRepository extends BaseRepository<Marker> {
  constructor(
    @InjectRepository(Marker)
    repository: Repository<Marker>,
  ) {
    super(repository);
  }

  async findOneByName(name: string): Promise<Marker | null> {
    return this.repository.findOne({
      where: { name },
    });
  }
}