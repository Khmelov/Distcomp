import { Test, TestingModule } from '@nestjs/testing';
import {
  NotFoundException,
  InternalServerErrorException,
} from '@nestjs/common';
import { StickersService } from './stickers.service';
import { PrismaService } from '../../../services/prisma.service';
import { StickerRequestTo } from '../../../dto/stickers/StickerRequestTo.dto';

describe('StickersService', () => {
  let service: StickersService;

  const mockSticker = {
    id: 1,
    name: 'Test Sticker',
  };

  const mockStickerRequest: StickerRequestTo = {
    name: 'Test Sticker',
  };

  const mockPrismaService = {
    sticker: {
      findUnique: jest.fn(),
      findMany: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
    },
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        StickersService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
      ],
    }).compile();

    service = module.get<StickersService>(StickersService);

    // Reset all mocks
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('createSticker', () => {
    it('should create a new sticker successfully', async () => {
      mockPrismaService.sticker.create.mockResolvedValue(mockSticker);

      const result = await service.createSticker(mockStickerRequest);

      expect(result).toEqual(mockSticker);
      expect(mockPrismaService.sticker.create).toHaveBeenCalledWith({
        data: mockStickerRequest,
      });
    });

    it('should create a sticker with special characters in name', async () => {
      const specialNameRequest: StickerRequestTo = {
        name: 'Special !@#$%^&*() Sticker',
      };
      const specialNameSticker = { id: 1, name: specialNameRequest.name };

      mockPrismaService.sticker.create.mockResolvedValue(specialNameSticker);

      const result = await service.createSticker(specialNameRequest);

      expect(result).toEqual(specialNameSticker);
      expect(result.name).toBe('Special !@#$%^&*() Sticker');
    });

    it('should create a sticker with very long name', async () => {
      const longName = 'a'.repeat(100);
      const longNameRequest: StickerRequestTo = { name: longName };
      const longNameSticker = { id: 1, name: longName };

      mockPrismaService.sticker.create.mockResolvedValue(longNameSticker);

      const result = await service.createSticker(longNameRequest);

      expect(result).toEqual(longNameSticker);
      expect(result.name.length).toBe(100);
    });

    it('should propagate database errors during creation', async () => {
      mockPrismaService.sticker.create.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(service.createSticker(mockStickerRequest)).rejects.toThrow(
        'Database error',
      );
    });
  });

  describe('getAll', () => {
    it('should return all stickers', async () => {
      const stickers = [mockSticker, { id: 2, name: 'Another Sticker' }];
      mockPrismaService.sticker.findMany.mockResolvedValue(stickers);

      const result = await service.getAll();

      expect(result).toEqual(stickers);
      expect(mockPrismaService.sticker.findMany).toHaveBeenCalledTimes(1);
    });

    it('should return empty array if no stickers exist', async () => {
      mockPrismaService.sticker.findMany.mockResolvedValue([]);

      const result = await service.getAll();

      expect(result).toEqual([]);
      expect(mockPrismaService.sticker.findMany).toHaveBeenCalledTimes(1);
    });

    it('should handle database errors during findMany', async () => {
      mockPrismaService.sticker.findMany.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(service.getAll()).rejects.toThrow('Database error');
    });
  });

  describe('getSticker', () => {
    it('should return a sticker by id', async () => {
      mockPrismaService.sticker.findUnique.mockResolvedValue(mockSticker);

      const result = await service.getSticker(1);

      expect(result).toEqual(mockSticker);
      expect(mockPrismaService.sticker.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should throw NotFoundException if sticker not found', async () => {
      mockPrismaService.sticker.findUnique.mockResolvedValue(null);

      await expect(service.getSticker(999)).rejects.toThrow(
        new NotFoundException('No sticker found'),
      );
      expect(mockPrismaService.sticker.findUnique).toHaveBeenCalledWith({
        where: { id: 999 },
      });
    });

    it('should handle database errors during find', async () => {
      mockPrismaService.sticker.findUnique.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(service.getSticker(1)).rejects.toThrow('Database error');
    });
  });

  describe('updateSticker', () => {
    it('should update a sticker successfully', async () => {
      const updateData: StickerRequestTo = {
        name: 'Updated Sticker',
      };
      const updatedSticker = { ...mockSticker, ...updateData };

      mockPrismaService.sticker.findUnique.mockResolvedValue(mockSticker);
      mockPrismaService.sticker.update.mockResolvedValue(updatedSticker);

      const result = await service.updateSticker(1, updateData);

      expect(result).toEqual(updatedSticker);
      expect(mockPrismaService.sticker.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(mockPrismaService.sticker.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: updateData,
      });
    });

    it('should throw NotFoundException if sticker not found', async () => {
      mockPrismaService.sticker.findUnique.mockResolvedValue(null);

      await expect(
        service.updateSticker(999, mockStickerRequest),
      ).rejects.toThrow(new NotFoundException('No sticker found'));
      expect(mockPrismaService.sticker.findUnique).toHaveBeenCalledWith({
        where: { id: 999 },
      });
      expect(mockPrismaService.sticker.update).not.toHaveBeenCalled();
    });

    it('should throw InternalServerErrorException on database error during update', async () => {
      mockPrismaService.sticker.findUnique.mockResolvedValue(mockSticker);
      mockPrismaService.sticker.update.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(
        service.updateSticker(1, mockStickerRequest),
      ).rejects.toThrow(InternalServerErrorException);
    });

    it('should update sticker with empty name if allowed', async () => {
      const updateData: StickerRequestTo = { name: '' };
      const updatedSticker = { ...mockSticker, name: '' };

      mockPrismaService.sticker.findUnique.mockResolvedValue(mockSticker);
      mockPrismaService.sticker.update.mockResolvedValue(updatedSticker);

      const result = await service.updateSticker(1, updateData);

      expect(result.name).toBe('');
    });
  });

  describe('deleteSticker', () => {
    it('should delete a sticker successfully', async () => {
      mockPrismaService.sticker.findUnique.mockResolvedValue(mockSticker);
      mockPrismaService.sticker.delete.mockResolvedValue(mockSticker);

      await service.deleteSticker(1);

      expect(mockPrismaService.sticker.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(mockPrismaService.sticker.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should throw NotFoundException if sticker not found', async () => {
      mockPrismaService.sticker.findUnique.mockResolvedValue(null);

      await expect(service.deleteSticker(999)).rejects.toThrow(
        new NotFoundException('No sticker found'),
      );
      expect(mockPrismaService.sticker.findUnique).toHaveBeenCalledWith({
        where: { id: 999 },
      });
      expect(mockPrismaService.sticker.delete).not.toHaveBeenCalled();
    });

    it('should propagate database errors on delete', async () => {
      mockPrismaService.sticker.findUnique.mockResolvedValue(mockSticker);
      mockPrismaService.sticker.delete.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(service.deleteSticker(1)).rejects.toThrow('Database error');
    });

    it('should handle deletion of sticker that is referenced elsewhere', async () => {
      // Prisma может выбросить ошибку внешнего ключа
      mockPrismaService.sticker.findUnique.mockResolvedValue(mockSticker);
      mockPrismaService.sticker.delete.mockRejectedValue(
        new Error('Foreign key constraint failed'),
      );

      await expect(service.deleteSticker(1)).rejects.toThrow(
        'Foreign key constraint failed',
      );
    });
  });

  // Тесты для проверки уникальности имени (если в БД есть ограничение)
  describe('uniqueness constraints', () => {
    it('should handle duplicate name error during creation', async () => {
      mockPrismaService.sticker.create.mockRejectedValue(
        new Error('Unique constraint failed on the fields: (`name`)'),
      );

      await expect(service.createSticker(mockStickerRequest)).rejects.toThrow(
        'Unique constraint failed',
      );
    });

    it('should handle duplicate name error during update', async () => {
      mockPrismaService.sticker.findUnique.mockResolvedValue(mockSticker);
      mockPrismaService.sticker.update.mockRejectedValue(
        new Error('Unique constraint failed on the fields: (`name`)'),
      );

      await expect(
        service.updateSticker(1, { name: 'duplicate' }),
      ).rejects.toThrow(InternalServerErrorException);
    });
  });
});
