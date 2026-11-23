import { Topic } from '@prisma/client';
import { prisma } from '../../db';
import { PrismaTopicRepository } from '../PrismaTopicRepository';

jest.mock('../../db', () => ({
  prisma: {
    topic: {
      findMany: jest.fn(),
      findUnique: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
      count: jest.fn(),
    },
    marker: {
      upsert: jest.fn(),
      deleteMany: jest.fn(),
    },
    topicMarker: {
      createMany: jest.fn(),
      findMany: jest.fn(),
    },
    $transaction: jest.fn(),
  },
}));

const topicPrisma = prisma as unknown as {
  topic: {
    findMany: jest.Mock;
    findUnique: jest.Mock;
    create: jest.Mock;
    update: jest.Mock;
    delete: jest.Mock;
    count: jest.Mock;
  };
  $transaction: jest.Mock;
};

const transaction = {
  topic: {
    create: jest.fn(),
    delete: jest.fn(),
  },
  marker: {
    upsert: jest.fn(),
    deleteMany: jest.fn(),
  },
  topicMarker: {
    createMany: jest.fn(),
    findMany: jest.fn(),
  },
};

describe('PrismaTopicRepository', () => {
  let repository: PrismaTopicRepository;

  const topic = {
    id: 1,
    authorId: 10,
    title: 'Test topic',
    content: 'Test content',
    created: new Date('2026-01-01'),
    modified: new Date('2026-01-01'),
  } as Topic;

  beforeEach(() => {
    repository = new PrismaTopicRepository();
    jest.clearAllMocks();

    topicPrisma.$transaction.mockImplementation(
      (callback: (tx: typeof transaction) => Promise<unknown>) =>
        callback(transaction)
    );
  });

  describe('findAll', () => {
    it('should return all topics with default pagination', async () => {
      topicPrisma.topic.findMany.mockResolvedValue([topic]);

      const result = await repository.findAll();

      expect(result).toEqual([topic]);
      expect(topicPrisma.topic.findMany).toHaveBeenCalledWith({
        skip: 0,
        take: 10,
        orderBy: { id: 'asc' },
      });
    });

    it('should use provided pagination parameters', async () => {
      topicPrisma.topic.findMany.mockResolvedValue([topic]);

      await repository.findAll({
        page: 2,
        size: 5,
        sortBy: 'title',
        sortOrder: 'desc',
      });

      expect(topicPrisma.topic.findMany).toHaveBeenCalledWith({
        skip: 5,
        take: 5,
        orderBy: { title: 'desc' },
      });
    });
  });

  describe('findById', () => {
    it('should return topic when it exists', async () => {
      topicPrisma.topic.findUnique.mockResolvedValue(topic);

      const result = await repository.findById(1);

      expect(result).toEqual(topic);
      expect(topicPrisma.topic.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should return undefined when topic does not exist', async () => {
      topicPrisma.topic.findUnique.mockResolvedValue(null);

      const result = await repository.findById(999);

      expect(result).toBeUndefined();
    });
  });

  describe('findByAuthorId', () => {
    it('should return topics belonging to an author', async () => {
      topicPrisma.topic.findMany.mockResolvedValue([topic]);

      const result = await repository.findByAuthorId(10);

      expect(result).toEqual([topic]);
      expect(topicPrisma.topic.findMany).toHaveBeenCalledWith({
        where: { authorId: 10 },
      });
    });

    it('should return an empty array when author has no topics', async () => {
      topicPrisma.topic.findMany.mockResolvedValue([]);

      const result = await repository.findByAuthorId(10);

      expect(result).toEqual([]);
    });
  });

  describe('save', () => {
    it('should create and return a topic', async () => {
      const topicData = {
        authorId: 10,
        title: 'Test topic',
        content: 'Test content',
        created: new Date('2026-01-01'),
        modified: new Date('2026-01-01'),
      };

      topicPrisma.topic.create.mockResolvedValue(topic);

      const result = await repository.save(topicData);

      expect(result).toEqual(topic);
      expect(topicPrisma.topic.create).toHaveBeenCalledWith({
        data: topicData,
      });
    });
  });

  describe('saveWithMarkers', () => {
    it('should create a topic and connect existing markers', async () => {
      const topicData = {
        authorId: 10,
        title: 'Test topic',
        content: 'Test content',
        created: new Date('2026-01-01'),
        modified: new Date('2026-01-01'),
      };

      const marker1 = { id: 1, name: 'JavaScript' };
      const marker2 = { id: 2, name: 'TypeScript' };

      transaction.topic.create.mockResolvedValue(topic);
      transaction.marker.upsert
        .mockResolvedValueOnce(marker1)
        .mockResolvedValueOnce(marker2);
      transaction.topicMarker.createMany.mockResolvedValue({ count: 2 });

      const result = await repository.saveWithMarkers(topicData, [
        'JavaScript',
        'TypeScript',
      ]);

      expect(result).toEqual(topic);

      expect(topicPrisma.$transaction).toHaveBeenCalledTimes(1);

      expect(transaction.topic.create).toHaveBeenCalledWith({
        data: topicData,
      });

      expect(transaction.marker.upsert).toHaveBeenNthCalledWith(1, {
        where: { name: 'JavaScript' },
        update: {},
        create: { name: 'JavaScript' },
      });

      expect(transaction.marker.upsert).toHaveBeenNthCalledWith(2, {
        where: { name: 'TypeScript' },
        update: {},
        create: { name: 'TypeScript' },
      });

      expect(transaction.topicMarker.createMany).toHaveBeenCalledWith({
        data: [
          { topicId: 1, markerId: 1 },
          { topicId: 1, markerId: 2 },
        ],
        skipDuplicates: true,
      });
    });

    it('should create a topic without markers', async () => {
      const topicData = {
        authorId: 10,
        title: 'Test topic',
        content: 'Test content',
        created: new Date('2026-01-01'),
        modified: new Date('2026-01-01'),
      };

      transaction.topic.create.mockResolvedValue(topic);

      const result = await repository.saveWithMarkers(topicData, []);

      expect(result).toEqual(topic);
      expect(transaction.topic.create).toHaveBeenCalledWith({
        data: topicData,
      });
      expect(transaction.marker.upsert).not.toHaveBeenCalled();
      expect(transaction.topicMarker.createMany).not.toHaveBeenCalled();
    });
  });

  describe('update', () => {
    it('should update and return a topic', async () => {
      const updatedTopic = {
        ...topic,
        title: 'Updated topic',
      };

      topicPrisma.topic.update.mockResolvedValue(updatedTopic);

      const result = await repository.update(1, {
        title: 'Updated topic',
      });

      expect(result).toEqual(updatedTopic);
      expect(topicPrisma.topic.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: { title: 'Updated topic' },
      });
    });

    it('should return undefined when update fails', async () => {
      topicPrisma.topic.update.mockRejectedValue(new Error('Record not found'));

      const result = await repository.update(999, {
        title: 'Updated topic',
      });

      expect(result).toBeUndefined();
    });
  });

  describe('delete', () => {
    it('should delete a topic and its unused markers', async () => {
      transaction.topicMarker.findMany.mockResolvedValue([
        { markerId: 1 },
        { markerId: 2 },
      ]);
      transaction.topic.delete.mockResolvedValue(topic);
      transaction.marker.deleteMany.mockResolvedValue({ count: 2 });

      const result = await repository.delete(1);

      expect(result).toBe(true);

      expect(topicPrisma.$transaction).toHaveBeenCalledTimes(1);

      expect(transaction.topicMarker.findMany).toHaveBeenCalledWith({
        where: { topicId: 1 },
        select: { markerId: true },
      });

      expect(transaction.topic.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });

      expect(transaction.marker.deleteMany).toHaveBeenCalledWith({
        where: {
          id: { in: [1, 2] },
          topics: { none: {} },
        },
      });
    });

    it('should delete a topic without markers', async () => {
      transaction.topicMarker.findMany.mockResolvedValue([]);
      transaction.topic.delete.mockResolvedValue(topic);

      const result = await repository.delete(1);

      expect(result).toBe(true);
      expect(transaction.topic.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(transaction.marker.deleteMany).not.toHaveBeenCalled();
    });

    it('should return false when deletion fails', async () => {
      transaction.topicMarker.findMany.mockResolvedValue([]);
      transaction.topic.delete.mockRejectedValue(new Error('Record not found'));

      const result = await repository.delete(999);

      expect(result).toBe(false);
    });
  });

  describe('existsById', () => {
    it('should return true when topic exists', async () => {
      topicPrisma.topic.count.mockResolvedValue(1);

      const result = await repository.existsById(1);

      expect(result).toBe(true);
      expect(topicPrisma.topic.count).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should return false when topic does not exist', async () => {
      topicPrisma.topic.count.mockResolvedValue(0);

      const result = await repository.existsById(999);

      expect(result).toBe(false);
    });
  });

  describe('existsByTitle', () => {
    it('should return true when topic with title exists', async () => {
      topicPrisma.topic.count.mockResolvedValue(1);

      const result = await repository.existsByTitle('Test topic');

      expect(result).toBe(true);
      expect(topicPrisma.topic.count).toHaveBeenCalledWith({
        where: { title: 'Test topic' },
      });
    });

    it('should return false when topic with title does not exist', async () => {
      topicPrisma.topic.count.mockResolvedValue(0);

      const result = await repository.existsByTitle('Unknown topic');

      expect(result).toBe(false);
    });
  });
});
