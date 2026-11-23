import { Message } from '@prisma/client';
import { prisma } from '../../db';
import { PrismaMessageRepository } from '../PrismaMessageRepository';

jest.mock('../../db', () => ({
  prisma: {
    message: {
      findMany: jest.fn(),
      findUnique: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      count: jest.fn(),
    },
  },
}));

const messagePrisma = prisma.message as unknown as {
  findMany: jest.Mock;
  findUnique: jest.Mock;
  create: jest.Mock;
  update: jest.Mock;
  delete: jest.Mock;
  count: jest.Mock;
};

describe('PrismaMessageRepository', () => {
  let repository: PrismaMessageRepository;

  const message = {
    id: 1,
    topicId: 10,
    content: 'Test message',
  } as Message;

  beforeEach(() => {
    repository = new PrismaMessageRepository();
    jest.clearAllMocks();
  });

  describe('findAll', () => {
    it('should return all messages with default pagination', async () => {
      messagePrisma.findMany.mockResolvedValue([message]);

      const result = await repository.findAll();

      expect(result).toEqual([message]);
      expect(messagePrisma.findMany).toHaveBeenCalledWith({
        skip: 0,
        take: 10,
        orderBy: { id: 'asc' },
      });
    });

    it('should use provided pagination parameters', async () => {
      messagePrisma.findMany.mockResolvedValue([message]);

      await repository.findAll({
        page: 2,
        size: 5,
        sortBy: 'id',
        sortOrder: 'desc',
      });

      expect(messagePrisma.findMany).toHaveBeenCalledWith({
        skip: 5,
        take: 5,
        orderBy: { id: 'desc' },
      });
    });
  });

  describe('findById', () => {
    it('should return message when it exists', async () => {
      messagePrisma.findUnique.mockResolvedValue(message);

      const result = await repository.findById(1);

      expect(result).toEqual(message);
      expect(messagePrisma.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should return undefined when message does not exist', async () => {
      messagePrisma.findUnique.mockResolvedValue(null);

      const result = await repository.findById(999);

      expect(result).toBeUndefined();
    });
  });

  describe('findByTopicId', () => {
    it('should return messages belonging to a topic', async () => {
      messagePrisma.findMany.mockResolvedValue([message]);

      const result = await repository.findByTopicId(10);

      expect(result).toEqual([message]);
      expect(messagePrisma.findMany).toHaveBeenCalledWith({
        where: { topicId: 10 },
      });
    });

    it('should return an empty array when topic has no messages', async () => {
      messagePrisma.findMany.mockResolvedValue([]);

      const result = await repository.findByTopicId(10);

      expect(result).toEqual([]);
    });
  });

  describe('save', () => {
    it('should create and return a message', async () => {
      const messageData = {
        topicId: 10,
        content: 'Test message',
      };

      messagePrisma.create.mockResolvedValue(message);

      const result = await repository.save(messageData);

      expect(result).toEqual(message);
      expect(messagePrisma.create).toHaveBeenCalledWith({
        data: messageData,
      });
    });
  });

  describe('update', () => {
    it('should update and return a message', async () => {
      const updatedMessage = {
        ...message,
        content: 'Updated message',
      };

      messagePrisma.update.mockResolvedValue(updatedMessage);

      const result = await repository.update(1, {
        content: 'Updated message',
      });

      expect(result).toEqual(updatedMessage);
      expect(messagePrisma.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: { content: 'Updated message' },
      });
    });

    it('should return undefined when update fails', async () => {
      messagePrisma.update.mockRejectedValue(new Error('Record not found'));

      const result = await repository.update(999, {
        content: 'Updated message',
      });

      expect(result).toBeUndefined();
    });
  });

  describe('delete', () => {
    it('should return true when message is deleted', async () => {
      messagePrisma.delete.mockResolvedValue(message);

      const result = await repository.delete(1);

      expect(result).toBe(true);
      expect(messagePrisma.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should return false when deletion fails', async () => {
      messagePrisma.delete.mockRejectedValue(new Error('Record not found'));

      const result = await repository.delete(999);

      expect(result).toBe(false);
    });
  });

  describe('existsById', () => {
    it('should return true when message exists', async () => {
      messagePrisma.count.mockResolvedValue(1);

      const result = await repository.existsById(1);

      expect(result).toBe(true);
      expect(messagePrisma.count).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should return false when message does not exist', async () => {
      messagePrisma.count.mockResolvedValue(0);

      const result = await repository.existsById(999);

      expect(result).toBe(false);
    });
  });
});
