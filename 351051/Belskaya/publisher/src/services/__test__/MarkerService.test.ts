import { Marker } from '@prisma/client';
import { ValidationError, NotFoundError } from '../../errors/AppError';
import { PrismaMarkerRepository } from '../../repositories/PrismaMarkerRepository';
import { MarkerService } from '../MarkerService';

describe('MarkerService', () => {
  const marker: Marker = {
    id: 1,
    name: 'red',
  };

  const validDto = {
    name: 'red',
  };

  const repository = {
    save: jest.fn(),
    findAll: jest.fn(),
    findById: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
    findByTopicId: jest.fn(),
  };

  let service: MarkerService;

  beforeEach(() => {
    jest.clearAllMocks();

    service = new MarkerService(
      repository as unknown as PrismaMarkerRepository
    );
  });

  describe('create', () => {
    it('should create a marker successfully', async () => {
      repository.save.mockResolvedValue(marker);

      const result = await service.create(validDto);

      expect(repository.save).toHaveBeenCalledWith(validDto);
      expect(result).toEqual(marker);
    });

    it('should throw ValidationError when data is invalid', async () => {
      await expect(service.create({ name: 'a' })).rejects.toThrow(
        ValidationError
      );

      expect(repository.save).not.toHaveBeenCalled();
    });
  });

  describe('findAll', () => {
    it('should return all markers', async () => {
      repository.findAll.mockResolvedValue([marker]);

      const result = await service.findAll();

      expect(repository.findAll).toHaveBeenCalledWith(undefined);
      expect(result).toEqual([marker]);
    });

    it('should pass pagination parameters to repository', async () => {
      const params = {
        page: 2,
        size: 5,
        sortBy: 'name',
        sortOrder: 'desc' as const,
      };

      repository.findAll.mockResolvedValue([marker]);

      await service.findAll(params);

      expect(repository.findAll).toHaveBeenCalledWith(params);
    });
  });

  describe('findById', () => {
    it('should return a marker when found', async () => {
      repository.findById.mockResolvedValue(marker);

      const result = await service.findById(1);

      expect(repository.findById).toHaveBeenCalledWith(1);
      expect(result).toEqual(marker);
    });

    it('should throw NotFoundError when marker does not exist', async () => {
      repository.findById.mockResolvedValue(undefined);

      await expect(service.findById(999)).rejects.toThrow(NotFoundError);

      await expect(service.findById(999)).rejects.toMatchObject({
        errorCode: '40403',
      });
    });
  });

  describe('update', () => {
    it('should update a marker successfully', async () => {
      const updatedMarker = { ...marker, name: 'blue' };
      repository.update.mockResolvedValue(updatedMarker);

      const result = await service.update(1, { name: 'blue' });

      expect(repository.update).toHaveBeenCalledWith(1, {
        name: 'blue',
      });
      expect(result).toEqual(updatedMarker);
    });

    it('should throw NotFoundError when marker does not exist', async () => {
      repository.update.mockResolvedValue(undefined);

      await expect(service.update(999, validDto)).rejects.toThrow(
        NotFoundError
      );
    });

    it('should throw ValidationError when data is invalid', async () => {
      await expect(service.update(1, { name: 'a' })).rejects.toThrow(
        ValidationError
      );

      expect(repository.update).not.toHaveBeenCalled();
    });
  });

  describe('delete', () => {
    it('should delete a marker successfully', async () => {
      repository.delete.mockResolvedValue(true);

      await expect(service.delete(1)).resolves.toBeUndefined();

      expect(repository.delete).toHaveBeenCalledWith(1);
    });

    it('should throw NotFoundError when marker does not exist', async () => {
      repository.delete.mockResolvedValue(false);

      await expect(service.delete(999)).rejects.toThrow(NotFoundError);
    });
  });

  describe('findByTopicId', () => {
    it('should return markers associated with a topic', async () => {
      repository.findByTopicId.mockResolvedValue([marker]);

      const result = await service.findByTopicId(10);

      expect(repository.findByTopicId).toHaveBeenCalledWith(10);
      expect(result).toEqual([marker]);
    });

    it('should return an empty array when topic has no markers', async () => {
      repository.findByTopicId.mockResolvedValue([]);

      const result = await service.findByTopicId(10);

      expect(result).toEqual([]);
    });
  });
});
