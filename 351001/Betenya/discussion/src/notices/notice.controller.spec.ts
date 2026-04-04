import { Test, TestingModule } from '@nestjs/testing';
import { NotFoundException } from '@nestjs/common';
import { NoticesController } from './notices.controller';
import { NoticesService } from './notices.service';
import { NoticeRequestTo } from '../dto/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../dto/NoticeResponseTo.dto';

describe('NoticesController', () => {
  let controller: NoticesController;
  let service: NoticesService;

  const mockResponse: NoticeResponseTo = { id: 1, content: 'Test content', articleId: 1, state: 'PENDING' };
  const mockRequest: NoticeRequestTo = { content: 'Test content', articleId: 1 };

  const mockService = {
    createNotice: jest.fn(),
    getAll: jest.fn(),
    getNotice: jest.fn(),
    updateNotice: jest.fn(),
    deleteNotice: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [NoticesController],
      providers: [{ provide: NoticesService, useValue: mockService }],
    }).compile();

    controller = module.get<NoticesController>(NoticesController);
    service = module.get<NoticesService>(NoticesService);
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('createNotice', () => {
    it('returns created notice', async () => {
      mockService.createNotice.mockResolvedValue(mockResponse);

      const result = await controller.createNotice(mockRequest);

      expect(result).toEqual(mockResponse);
      expect(service.createNotice).toHaveBeenCalledWith(mockRequest);
    });

    it('propagates service errors', async () => {
      mockService.createNotice.mockRejectedValue(new Error('DB error'));

      await expect(controller.createNotice(mockRequest)).rejects.toThrow('DB error');
    });
  });

  describe('getAllNotices', () => {
    it('returns array of notices', async () => {
      const list = [mockResponse, { ...mockResponse, id: 2 }];
      mockService.getAll.mockResolvedValue(list);

      const result = await controller.getAllNotices();

      expect(result).toHaveLength(2);
      expect(service.getAll).toHaveBeenCalledTimes(1);
    });

    it('returns empty array when no notices', async () => {
      mockService.getAll.mockResolvedValue([]);

      expect(await controller.getAllNotices()).toEqual([]);
    });
  });

  describe('getNotice', () => {
    it('returns notice by id', async () => {
      mockService.getNotice.mockResolvedValue(mockResponse);

      const result = await controller.getNotice(1);

      expect(result).toEqual(mockResponse);
      expect(service.getNotice).toHaveBeenCalledWith(1);
    });

    it('throws NotFoundException for unknown id', async () => {
      mockService.getNotice.mockRejectedValue(new NotFoundException('Notice not found'));

      await expect(controller.getNotice(999)).rejects.toThrow(NotFoundException);
    });
  });

  describe('updateNotice', () => {
    it('returns updated notice', async () => {
      const updated = { ...mockResponse, content: 'Updated' };
      mockService.updateNotice.mockResolvedValue(updated);

      const result = await controller.updateNotice(1, { ...mockRequest, content: 'Updated' });

      expect(result.content).toBe('Updated');
      expect(service.updateNotice).toHaveBeenCalledWith(1, { ...mockRequest, content: 'Updated' });
    });

    it('throws NotFoundException when notice not found', async () => {
      mockService.updateNotice.mockRejectedValue(new NotFoundException('Notice not found'));

      await expect(controller.updateNotice(999, mockRequest)).rejects.toThrow(NotFoundException);
    });
  });

  describe('deleteNotice', () => {
    it('deletes successfully (void)', async () => {
      mockService.deleteNotice.mockResolvedValue(undefined);

      await expect(controller.deleteNotice(1)).resolves.toBeUndefined();
      expect(service.deleteNotice).toHaveBeenCalledWith(1);
    });

    it('throws NotFoundException when notice not found', async () => {
      mockService.deleteNotice.mockRejectedValue(new NotFoundException('Notice not found'));

      await expect(controller.deleteNotice(999)).rejects.toThrow(NotFoundException);
    });
  });
});
