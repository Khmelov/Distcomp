import { prisma, redisClient } from '../../db';
import { Author } from '../../generated/prisma/client';
import { PrismaAuthorRepository } from '../PrismaAuthorRepository';

jest.mock('../../db', () => ({
  prisma: {
    author: {
      findMany: jest.fn(),
      findUnique: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      count: jest.fn(),
    },
  },
  redisClient: {
    get: jest.fn(),
    set: jest.fn(),
    del: jest.fn(),
    keys: jest.fn().mockResolvedValue([]),
  },
}));

const authorModel = prisma.author as unknown as {
  findMany: jest.Mock;
  findUnique: jest.Mock;
  create: jest.Mock;
  update: jest.Mock;
  delete: jest.Mock;
  count: jest.Mock;
};

const redisGet = redisClient.get as jest.Mock;
const redisSet = redisClient.set as jest.Mock;
const redisDel = redisClient.del as jest.Mock;
const redisKeys = redisClient.keys as jest.Mock;

describe('PrismaAuthorRepository', () => {
  let repository: PrismaAuthorRepository;

  const author = {
    id: 1,
    login: 'test-login',
    password: 'hashed-password',
  } as Author;

  beforeEach(() => {
    repository = new PrismaAuthorRepository();
    jest.clearAllMocks();
    redisGet.mockResolvedValue(null);
    redisKeys.mockResolvedValue([]);
  });

  describe('findAll', () => {
    it('should return all authors with default pagination', async () => {
      authorModel.findMany.mockResolvedValue([author]);

      const result = await repository.findAll();

      expect(result).toEqual([author]);
      expect(authorModel.findMany).toHaveBeenCalledWith({
        skip: 0,
        take: 10,
        orderBy: { id: 'asc' },
      });
    });

    it('should return cached data if available', async () => {
      redisGet.mockResolvedValue(JSON.stringify([author]));

      const result = await repository.findAll();

      expect(result).toEqual([author]);
      expect(authorModel.findMany).not.toHaveBeenCalled();
    });

    it('should pass pagination parameters to Prisma', async () => {
      authorModel.findMany.mockResolvedValue([author]);

      await repository.findAll({
        page: 2,
        size: 5,
        sortBy: 'login',
        sortOrder: 'desc',
      });

      expect(authorModel.findMany).toHaveBeenCalledWith({
        skip: 5,
        take: 5,
        orderBy: { login: 'desc' },
      });
    });

    it('should return an empty array when there are no authors', async () => {
      authorModel.findMany.mockResolvedValue([]);

      const result = await repository.findAll();

      expect(result).toEqual([]);
    });
  });

  describe('findById', () => {
    it('should return an author by id', async () => {
      authorModel.findUnique.mockResolvedValue(author);

      const result = await repository.findById(1);

      expect(result).toEqual(author);
      expect(authorModel.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(redisSet).toHaveBeenCalled();
    });

    it('should return cached author if available', async () => {
      redisGet.mockResolvedValue(JSON.stringify(author));

      const result = await repository.findById(1);

      expect(result).toEqual(author);
      expect(authorModel.findUnique).not.toHaveBeenCalled();
    });

    it('should return undefined when author is not found', async () => {
      authorModel.findUnique.mockResolvedValue(null);

      const result = await repository.findById(999);

      expect(result).toBeUndefined();
    });
  });

  describe('findByLogin', () => {
    it('should return an author by login', async () => {
      authorModel.findUnique.mockResolvedValue(author);

      const result = await repository.findByLogin('test-login');

      expect(result).toEqual(author);
      expect(authorModel.findUnique).toHaveBeenCalledWith({
        where: { login: 'test-login' },
      });
    });

    it('should return undefined when author is not found', async () => {
      authorModel.findUnique.mockResolvedValue(null);

      const result = await repository.findByLogin('unknown');

      expect(result).toBeUndefined();
    });
  });

  describe('save', () => {
    it('should create and return an author', async () => {
      const authorData = {
        login: 'new-login',
        password: 'hashed-password',
      } as Omit<Author, 'id'>;

      authorModel.create.mockResolvedValue(author);

      const result = await repository.save(authorData);

      expect(result).toEqual(author);
      expect(authorModel.create).toHaveBeenCalledWith({
        data: authorData,
      });
      expect(redisKeys).toHaveBeenCalled();
    });
  });

  describe('update', () => {
    it('should update and return an author', async () => {
      const updateData = { login: 'updated-login' };
      const updatedAuthor = { ...author, ...updateData };

      authorModel.update.mockResolvedValue(updatedAuthor);

      const result = await repository.update(1, updateData);

      expect(result).toEqual(updatedAuthor);
      expect(authorModel.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: updateData,
      });
      expect(redisDel).toHaveBeenCalledWith('author:1');
    });

    it('should return undefined when Prisma throws an error', async () => {
      authorModel.update.mockRejectedValue(new Error('Database error'));

      const result = await repository.update(999, {
        login: 'updated-login',
      });

      expect(result).toBeUndefined();
    });
  });

  describe('delete', () => {
    it('should return true when author is deleted successfully', async () => {
      authorModel.delete.mockResolvedValue(author);

      const result = await repository.delete(1);

      expect(result).toBe(true);
      expect(authorModel.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(redisDel).toHaveBeenCalledWith('author:1');
    });

    it('should return false when Prisma throws an error', async () => {
      authorModel.delete.mockRejectedValue(new Error('Database error'));

      const result = await repository.delete(999);

      expect(result).toBe(false);
    });
  });

  describe('existsById', () => {
    it('should return true when author exists', async () => {
      authorModel.count.mockResolvedValue(1);

      const result = await repository.existsById(1);

      expect(result).toBe(true);
    });

    it('should return false when author does not exist', async () => {
      authorModel.count.mockResolvedValue(0);

      const result = await repository.existsById(999);

      expect(result).toBe(false);
    });
  });
});
