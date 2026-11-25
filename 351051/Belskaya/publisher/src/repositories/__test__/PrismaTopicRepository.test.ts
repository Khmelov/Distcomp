import { Topic } from '../../generated/prisma/client';
import { prisma, redisClient } from '../../db';
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
    topicMarker: {
      findMany: jest.fn(),
      createMany: jest.fn(),
    },
    marker: {
      upsert: jest.fn(),
      deleteMany: jest.fn(),
    },
    $transaction: jest.fn((cb) => cb(prisma)),
  },
  redisClient: {
    get: jest.fn(),
    set: jest.fn(),
    del: jest.fn(),
    keys: jest.fn().mockResolvedValue([]),
  },
}));

const topicModel = prisma.topic as unknown as {
  findMany: jest.Mock;
  findUnique: jest.Mock;
  create: jest.Mock;
  update: jest.Mock;
  delete: jest.Mock;
  count: jest.Mock;
};

const topicMarkerModel = prisma.topicMarker as unknown as {
  findMany: jest.Mock;
  createMany: jest.Mock;
};

const redisGet = redisClient.get as jest.Mock;
const redisSet = redisClient.set as jest.Mock;
const redisDel = redisClient.del as jest.Mock;

describe('PrismaTopicRepository', () => {
  let repository: PrismaTopicRepository;

  const topic = {
    id: 1,
    authorId: 1,
    title: 'Test topic',
    content: 'Test content',
    created: new Date(),
    modified: new Date(),
  } as unknown as Topic;

  const topicFromCache = JSON.parse(JSON.stringify(topic));

  beforeEach(() => {
    repository = new PrismaTopicRepository();
    jest.clearAllMocks();
    redisGet.mockResolvedValue(null);
    topicMarkerModel.findMany.mockResolvedValue([]);
  });

  describe('findAll', () => {
    it('should return all topics with default pagination', async () => {
      topicModel.findMany.mockResolvedValue([topic]);

      const result = await repository.findAll();

      expect(result).toEqual([topic]);
      expect(topicModel.findMany).toHaveBeenCalledWith({
        skip: 0,
        take: 10,
        orderBy: { id: 'asc' },
      });
    });

    it('should return cached data if available', async () => {
      redisGet.mockResolvedValue(JSON.stringify([topic]));

      const result = await repository.findAll();

      expect(result).toEqual([topicFromCache]);
      expect(topicModel.findMany).not.toHaveBeenCalled();
    });
  });

  describe('findById', () => {
    it('should return a topic by id', async () => {
      topicModel.findUnique.mockResolvedValue(topic);

      const result = await repository.findById(1);

      expect(result).toEqual(topic);
      expect(topicModel.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(redisSet).toHaveBeenCalled();
    });

    it('should return cached topic if available', async () => {
      redisGet.mockResolvedValue(JSON.stringify(topic));

      const result = await repository.findById(1);

      expect(result).toEqual(topicFromCache);
      expect(topicModel.findUnique).not.toHaveBeenCalled();
    });

    it('should return undefined when topic is not found', async () => {
      topicModel.findUnique.mockResolvedValue(null);

      const result = await repository.findById(999);

      expect(result).toBeUndefined();
    });
  });

  describe('findByAuthorId', () => {
    it('should return topics by authorId', async () => {
      topicModel.findMany.mockResolvedValue([topic]);

      const result = await repository.findByAuthorId(1);

      expect(result).toEqual([topic]);
      expect(topicModel.findMany).toHaveBeenCalledWith({
        where: { authorId: 1 },
      });
    });
  });

  describe('save', () => {
    it('should create and return a topic', async () => {
      const topicData = {
        authorId: 1,
        title: 'New topic',
        content: 'New content',
      } as Omit<Topic, 'id'>;

      topicModel.create.mockResolvedValue(topic);

      const result = await repository.save(topicData);

      expect(result).toEqual(topic);
      expect(topicModel.create).toHaveBeenCalledWith({ data: topicData });
    });
  });

  describe('update', () => {
    it('should update and return a topic', async () => {
      const updateData = { title: 'Updated title' };
      const updatedTopic = { ...topic, ...updateData };

      topicModel.update.mockResolvedValue(updatedTopic);

      const result = await repository.update(1, updateData);

      expect(result).toEqual(updatedTopic);
      expect(topicModel.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: updateData,
      });
      expect(redisDel).toHaveBeenCalledWith('topic:1');
    });

    it('should return undefined when update fails', async () => {
      topicModel.update.mockRejectedValue(new Error('Error'));

      const result = await repository.update(999, { title: 'Updated' });

      expect(result).toBeUndefined();
    });
  });

  describe('delete', () => {
    it('should return true when topic is deleted', async () => {
      topicModel.findUnique.mockResolvedValue(topic);
      topicModel.delete.mockResolvedValue(topic);
      topicMarkerModel.findMany.mockResolvedValue([]);

      const result = await repository.delete(1);

      expect(result).toBe(true);
      expect(topicModel.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(redisDel).toHaveBeenCalledWith('topic:1');
    });

    it('should return false when deletion fails', async () => {
      topicModel.findUnique.mockResolvedValue(topic);
      topicModel.delete.mockRejectedValue(new Error('Error'));

      const result = await repository.delete(999);

      expect(result).toBe(false);
    });
  });

  describe('existsById', () => {
    it('should return true when topic exists', async () => {
      topicModel.count.mockResolvedValue(1);

      const result = await repository.existsById(1);

      expect(result).toBe(true);
    });

    it('should return false when topic does not exist', async () => {
      topicModel.count.mockResolvedValue(0);

      const result = await repository.existsById(999);

      expect(result).toBe(false);
    });
  });
});
