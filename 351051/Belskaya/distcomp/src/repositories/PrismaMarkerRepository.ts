import { prisma } from '../db';
import { Marker } from '../entities';
import { IRepository } from './IRepository';

export class PrismaMarkerRepository implements IRepository<Marker> {
  async findAll(): Promise<Marker[]> {
    return prisma.marker.findMany();
  }
  async findById(id: number): Promise<Marker | undefined> {
    const marker = await prisma.marker.findUnique({ where: { id } });
    return marker || undefined;
  }
  async findByTopicId(topicId: number): Promise<Marker[]> {
    return prisma.marker.findMany({
      where: { topics: { some: { topicId } } },
    });
  }
  async save(entity: Omit<Marker, 'id'>): Promise<Marker> {
    return prisma.marker.create({ data: entity });
  }
  async update(
    id: number,
    entity: Partial<Marker>
  ): Promise<Marker | undefined> {
    try {
      return await prisma.marker.update({ where: { id }, data: entity });
    } catch {
      return undefined;
    }
  }
  async delete(id: number): Promise<boolean> {
    try {
      await prisma.marker.delete({ where: { id } });
      return true;
    } catch {
      return false;
    }
  }
  async existsById(id: number): Promise<boolean> {
    return (await prisma.marker.count({ where: { id } })) > 0;
  }
}
