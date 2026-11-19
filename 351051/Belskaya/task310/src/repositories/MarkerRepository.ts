import { Marker } from '../entities';
import { InMemoryRepository } from './InMemoryRepository';

export class MarkerRepository extends InMemoryRepository<Marker> {
  findByName(name: string): Marker | undefined {
    return this.findAll().find((m) => m.name === name);
  }
}