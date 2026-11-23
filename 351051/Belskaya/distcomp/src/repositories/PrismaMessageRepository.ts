import { prisma } from '../db';
import { Message } from '../entities';
import { IRepository } from './IRepository';

export class PrismaMessageRepository implements IRepository<Message> {
  async findAll(): Promise<Message[]> {
    return prisma.message.findMany();
  }
  async findById(id: number): Promise<Message | undefined> {
    const msg = await prisma.message.findUnique({ where: { id } });
    return msg || undefined;
  }
  async findByTopicId(topicId: number): Promise<Message[]> {
    return prisma.message.findMany({ where: { topicId } });
  }
  async save(entity: Omit<Message, 'id'>): Promise<Message> {
    return prisma.message.create({ data: entity });
  }
  async update(
    id: number,
    entity: Partial<Message>
  ): Promise<Message | undefined> {
    try {
      return await prisma.message.update({ where: { id }, data: entity });
    } catch {
      return undefined;
    }
  }
  async delete(id: number): Promise<boolean> {
    try {
      await prisma.message.delete({ where: { id } });
      return true;
    } catch {
      return false;
    }
  }
  async existsById(id: number): Promise<boolean> {
    return (await prisma.message.count({ where: { id } })) > 0;
  }
}
