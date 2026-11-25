import { prisma } from '../db';
import { Topic } from '../entities';
import { getPaginationOptions } from '../utils/getPaginationOptions';
import { IRepository, PaginationParams } from './IRepository';

export class PrismaTopicRepository implements IRepository<Topic> {
  async findAll(params?: PaginationParams): Promise<Topic[]> {
    return prisma.topic.findMany(getPaginationOptions(params));
  }

  async findById(id: number): Promise<Topic | undefined> {
    const topic = await prisma.topic.findUnique({ where: { id } });
    return topic || undefined;
  }

  async findByAuthorId(authorId: number): Promise<Topic[]> {
    return prisma.topic.findMany({ where: { authorId } });
  }

  async save(entity: Omit<Topic, 'id'>): Promise<Topic> {
    return prisma.topic.create({ data: entity });
  }

  async saveWithMarkers(
    entity: Omit<Topic, 'id'>,
    markerNames: string[]
  ): Promise<Topic> {
    return prisma.$transaction(async (tx) => {
      const topic = await tx.topic.create({
        data: entity,
      });

      const markers = await Promise.all(
        markerNames.map((name) =>
          tx.marker.upsert({
            where: { name },
            update: {},
            create: { name },
          })
        )
      );

      if (markers.length > 0) {
        await tx.topicMarker.createMany({
          data: markers.map((marker) => ({
            topicId: topic.id,
            markerId: marker.id,
          })),
          skipDuplicates: true,
        });
      }

      return topic;
    });
  }

  async update(id: number, entity: Partial<Topic>): Promise<Topic | undefined> {
    try {
      return await prisma.topic.update({ where: { id }, data: entity });
    } catch {
      return undefined;
    }
  }

  async delete(id: number): Promise<boolean> {
    try {
      await prisma.$transaction(async (tx) => {
        const topicMarkers = await tx.topicMarker.findMany({
          where: { topicId: id },
          select: { markerId: true },
        });

        await tx.topic.delete({
          where: { id },
        });

        if (topicMarkers.length > 0) {
          await tx.marker.deleteMany({
            where: {
              id: {
                in: topicMarkers.map((item) => item.markerId),
              },
              topics: {
                none: {},
              },
            },
          });
        }
      });

      return true;
    } catch {
      return false;
    }
  }

  async existsById(id: number): Promise<boolean> {
    return (await prisma.topic.count({ where: { id } })) > 0;
  }

  async existsByTitle(title: string): Promise<boolean> {
    const count = await prisma.topic.count({
      where: { title },
    });

    return count > 0;
  }
}
