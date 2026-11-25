import { Marker } from '../../generated/prisma/client';
import { prisma, redisClient } from '../../db';
import { PrismaMarkerRepository } from '../PrismaMarkerRepository';

jest.mock('../../db', () => ({
  prisma: {
    marker: {
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

const markerPrisma = prisma.marker as unknown as {
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

describe('PrismaMarkerRepository', () => {
  let repository: PrismaMarkerRepository;

  const marker = {
    id: 1,
    name: 'JavaScript',
  } as Marker;

  beforeEach(() => {
    repository = new PrismaMarkerRepository();
    jest.clearAllMocks();
    redisGet.mockResolvedValue(null);
  });

  describe('findAll', () => {
    it('should return all markers with default pagination', async () => {
      markerPrisma.findMany.mockResolvedValue([marker]);

      const result = await repository.findAll();

      expect(result).toEqual([marker]);
      expect(markerPrisma.findMany).toHaveBeenCalledWith({
        skip: 0,
        take: 10,
        orderBy: { id: 'asc' },
      });
    });

    it('should return cached data if available', async () => {
      redisGet.mockResolvedValue(JSON.stringify([marker]));

      const result = await repository.findAll();

      expect(result).toEqual([marker]);
      expect(markerPrisma.findMany).not.toHaveBeenCalled();
    });
  });

  describe('findById', () => {
    it('should return marker when it exists', async () => {
      markerPrisma.findUnique.mockResolvedValue(marker);

      const result = await repository.findById(1);

      expect(result).toEqual(marker);
      expect(markerPrisma.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(redisSet).toHaveBeenCalled();
    });

    it('should return cached marker if available', async () => {
      redisGet.mockResolvedValue(JSON.stringify(marker));

      const result = await repository.findById(1);

      expect(result).toEqual(marker);
      expect(markerPrisma.findUnique).not.toHaveBeenCalled();
    });

    it('should return undefined when marker does not exist', async () => {
      markerPrisma.findUnique.mockResolvedValue(null);

      const result = await repository.findById(999);

      expect(result).toBeUndefined();
    });
  });

  describe('findByTopicId', () => {
    it('should return markers belonging to a topic', async () => {
      markerPrisma.findMany.mockResolvedValue([marker]);

      const result = await repository.findByTopicId(10);

      expect(result).toEqual([marker]);
    });
  });

  describe('save', () => {
    it('should create and return a marker', async () => {
      const markerData = { name: 'JavaScript' };
      markerPrisma.create.mockResolvedValue(marker);

      const result = await repository.save(markerData);

      expect(result).toEqual(marker);
      expect(markerPrisma.create).toHaveBeenCalledWith({
        data: markerData,
      });
    });
  });

  describe('update', () => {
    it('should update and return a marker', async () => {
      const updatedMarker = { ...marker, name: 'TypeScript' };

      markerPrisma.update.mockResolvedValue(updatedMarker);

      const result = await repository.update(1, { name: 'TypeScript' });

      expect(result).toEqual(updatedMarker);
      expect(markerPrisma.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: { name: 'TypeScript' },
      });
      expect(redisDel).toHaveBeenCalledWith('marker:1');
    });

    it('should return undefined when update fails', async () => {
      markerPrisma.update.mockRejectedValue(new Error('Record not found'));

      const result = await repository.update(999, { name: 'TypeScript' });

      expect(result).toBeUndefined();
    });
  });

  describe('delete', () => {
    it('should return true when marker is deleted', async () => {
      markerPrisma.delete.mockResolvedValue(marker);

      const result = await repository.delete(1);

      expect(result).toBe(true);
      expect(markerPrisma.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(redisDel).toHaveBeenCalledWith('marker:1');
    });

    it('should return false when deletion fails', async () => {
      markerPrisma.delete.mockRejectedValue(new Error('Record not found'));

      const result = await repository.delete(999);

      expect(result).toBe(false);
    });
  });

  describe('existsById', () => {
    it('should return true when marker exists', async () => {
      markerPrisma.count.mockResolvedValue(1);

      const result = await repository.existsById(1);

      expect(result).toBe(true);
    });

    it('should return false when marker does not exist', async () => {
      markerPrisma.count.mockResolvedValue(0);

      const result = await repository.existsById(999);

      expect(result).toBe(false);
    });
  });
});
