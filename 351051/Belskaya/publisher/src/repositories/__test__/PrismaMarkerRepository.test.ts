import { Marker } from '../../generated/prisma/client';
import { prisma } from '../../db';
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
}));

const markerPrisma = prisma.marker as unknown as {
  findMany: jest.Mock;
  findUnique: jest.Mock;
  create: jest.Mock;
  update: jest.Mock;
  delete: jest.Mock;
  count: jest.Mock;
};

describe('PrismaMarkerRepository', () => {
  let repository: PrismaMarkerRepository;

  const marker = {
    id: 1,
    name: 'JavaScript',
  } as Marker;

  beforeEach(() => {
    repository = new PrismaMarkerRepository();
    jest.clearAllMocks();
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

    it('should use provided pagination parameters', async () => {
      markerPrisma.findMany.mockResolvedValue([marker]);

      await repository.findAll({
        page: 2,
        size: 5,
        sortBy: 'name',
        sortOrder: 'desc',
      });

      expect(markerPrisma.findMany).toHaveBeenCalledWith({
        skip: 5,
        take: 5,
        orderBy: { name: 'desc' },
      });
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
      expect(markerPrisma.findMany).toHaveBeenCalledWith({
        where: {
          topics: {
            some: { topicId: 10 },
          },
        },
      });
    });

    it('should return an empty array when topic has no markers', async () => {
      markerPrisma.findMany.mockResolvedValue([]);

      const result = await repository.findByTopicId(10);

      expect(result).toEqual([]);
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
      const updatedMarker = {
        ...marker,
        name: 'TypeScript',
      };

      markerPrisma.update.mockResolvedValue(updatedMarker);

      const result = await repository.update(1, {
        name: 'TypeScript',
      });

      expect(result).toEqual(updatedMarker);
      expect(markerPrisma.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: { name: 'TypeScript' },
      });
    });

    it('should return undefined when update fails', async () => {
      markerPrisma.update.mockRejectedValue(new Error('Record not found'));

      const result = await repository.update(999, {
        name: 'TypeScript',
      });

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
      expect(markerPrisma.count).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should return false when marker does not exist', async () => {
      markerPrisma.count.mockResolvedValue(0);

      const result = await repository.existsById(999);

      expect(result).toBe(false);
    });
  });
});
