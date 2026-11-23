import { prisma } from '../db';
import { Author } from '../entities';
import { getPaginationOptions } from '../utils/getPaginationOptions';
import { IRepository, PaginationParams } from './IRepository';

export class PrismaAuthorRepository implements IRepository<Author> {
  async findAll(params?: PaginationParams): Promise<Author[]> {
    return prisma.author.findMany(getPaginationOptions(params));
  }

  async findById(id: number): Promise<Author | undefined> {
    const author = await prisma.author.findUnique({ where: { id } });
    return author || undefined;
  }

  async findByLogin(login: string): Promise<Author | undefined> {
    const author = await prisma.author.findUnique({ where: { login } });
    return author || undefined;
  }

  async save(entity: Omit<Author, 'id'>): Promise<Author> {
    return prisma.author.create({ data: entity });
  }

  async update(
    id: number,
    entity: Partial<Author>
  ): Promise<Author | undefined> {
    try {
      return await prisma.author.update({ where: { id }, data: entity });
    } catch {
      return undefined;
    }
  }

  async delete(id: number): Promise<boolean> {
    try {
      await prisma.author.delete({ where: { id } });
      return true;
    } catch {
      return false;
    }
  }

  async existsById(id: number): Promise<boolean> {
    const count = await prisma.author.count({ where: { id } });
    return count > 0;
  }
}
