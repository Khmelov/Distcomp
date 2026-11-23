import { prisma } from '../db';
import { Topic } from '../entities';
import { IRepository } from './IRepository';

export class PrismaTopicRepository implements IRepository<Topic> {
  async findAll(): Promise<Topic[]> {
    return prisma.topic.findMany();
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
  async update(id: number, entity: Partial<Topic>): Promise<Topic | undefined> {
    try {
      return await prisma.topic.update({ where: { id }, data: entity });
    } catch {
      return undefined;
    }
  }
  async delete(id: number): Promise<boolean> {
    try {
      await prisma.topic.delete({ where: { id } });
      return true;
    } catch {
      return false;
    }
  }
  async existsById(id: number): Promise<boolean> {
    return (await prisma.topic.count({ where: { id } })) > 0;
  }
}
