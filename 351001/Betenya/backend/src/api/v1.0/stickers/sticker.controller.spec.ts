import { Test, TestingModule } from '@nestjs/testing';
import { StickersController } from './stickers.controller';
import { StickersService } from './stickers.service';
import { StickerRequestTo } from '../../../dto/stickers/StickerRequestTo.dto';
import { StickerResponseTo } from '../../../dto/stickers/StickerResponseTo.dto';
import { NotFoundException } from '@nestjs/common';

describe('StickersController', () => {
  let controller: StickersController;
  let service: StickersService;

  const mockStickerResponse: StickerResponseTo = {
    id: BigInt(1),
    name: 'Test Sticker',
  };

  const mockStickerRequest: StickerRequestTo = {
    name: 'Test Sticker',
  };

  const mockStickersService = {
    createSticker: jest.fn(),
    getAll: jest.fn(),
    getSticker: jest.fn(),
    updateSticker: jest.fn(),
    deleteSticker: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [StickersController],
      providers: [
        {
          provide: StickersService,
          useValue: mockStickersService,
        },
      ],
    }).compile();

    controller = module.get<StickersController>(StickersController);
    service = module.get<StickersService>(StickersService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('createSticker', () => {
    it('should create a new sticker successfully', async () => {
      mockStickersService.createSticker.mockResolvedValue(mockStickerResponse);

      const result = await controller.createSticker(mockStickerRequest);

      expect(result).toEqual(mockStickerResponse);
      expect(service.createSticker).toHaveBeenCalledWith(mockStickerRequest);
      expect(service.createSticker).toHaveBeenCalledTimes(1);
    });

    it('should handle database errors during creation', async () => {
      mockStickersService.createSticker.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(
        controller.createSticker(mockStickerRequest),
      ).rejects.toThrow('Database error');
    });
  });

  describe('getAllStickers', () => {
    it('should return an array of stickers', async () => {
      const stickers = [
        mockStickerResponse,
        { id: 2, name: 'Another Sticker' },
      ];
      mockStickersService.getAll.mockResolvedValue(stickers);

      const result = await controller.getAllStickers();

      expect(result).toEqual(stickers);
      expect(service.getAll).toHaveBeenCalledTimes(1);
    });

    it('should return empty array when no stickers exist', async () => {
      mockStickersService.getAll.mockResolvedValue([]);

      const result = await controller.getAllStickers();

      expect(result).toEqual([]);
      expect(service.getAll).toHaveBeenCalledTimes(1);
    });
  });

  describe('getStickerById', () => {
    it('should return a sticker by id', async () => {
      mockStickersService.getSticker.mockResolvedValue(mockStickerResponse);

      const result = await controller.getStickerById(1);

      expect(result).toEqual(mockStickerResponse);
      expect(service.getSticker).toHaveBeenCalledWith(1);
      expect(service.getSticker).toHaveBeenCalledTimes(1);
    });

    it('should throw NotFoundException when sticker not found', async () => {
      mockStickersService.getSticker.mockRejectedValue(
        new NotFoundException('No sticker found'),
      );

      await expect(controller.getStickerById(999)).rejects.toThrow(
        NotFoundException,
      );
      expect(service.getSticker).toHaveBeenCalledWith(999);
    });
  });

  describe('updateSticker', () => {
    it('should update a sticker successfully', async () => {
      const updateData: StickerRequestTo = {
        name: 'Updated Sticker',
      };
      const updatedSticker = { ...mockStickerResponse, ...updateData };

      mockStickersService.updateSticker.mockResolvedValue(updatedSticker);

      const result = await controller.updateSticker(1, updateData);

      expect(result).toEqual(updatedSticker);
      expect(service.updateSticker).toHaveBeenCalledWith(1, updateData);
      expect(service.updateSticker).toHaveBeenCalledTimes(1);
    });

    it('should throw NotFoundException when sticker not found', async () => {
      mockStickersService.updateSticker.mockRejectedValue(
        new NotFoundException('No sticker found'),
      );

      await expect(
        controller.updateSticker(999, mockStickerRequest),
      ).rejects.toThrow(NotFoundException);
    });

    it('should handle database errors during update', async () => {
      mockStickersService.updateSticker.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(
        controller.updateSticker(1, mockStickerRequest),
      ).rejects.toThrow('Database error');
    });
  });

  describe('deleteSticker', () => {
    it('should delete a sticker successfully', async () => {
      mockStickersService.deleteSticker.mockResolvedValue(undefined);

      const result = await controller.deleteSticker(1);

      expect(result).toBeUndefined();
      expect(service.deleteSticker).toHaveBeenCalledWith(1);
      expect(service.deleteSticker).toHaveBeenCalledTimes(1);
    });

    it('should throw NotFoundException when sticker not found', async () => {
      mockStickersService.deleteSticker.mockRejectedValue(
        new NotFoundException('No sticker found'),
      );

      await expect(controller.deleteSticker(999)).rejects.toThrow(
        NotFoundException,
      );
      expect(service.deleteSticker).toHaveBeenCalledWith(999);
    });
  });
});
