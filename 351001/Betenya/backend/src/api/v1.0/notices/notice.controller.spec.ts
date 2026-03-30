import { Test, TestingModule } from '@nestjs/testing';
import { NoticesController } from './notices.controller';
import { NoticesService } from './notices.service';
import { NoticeRequestTo } from '../../../dto/notices/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../../../dto/notices/NoticeResponseTo.dto';
import { NotFoundException } from '@nestjs/common';

describe('NoticesController', () => {
  let controller: NoticesController;
  let service: NoticesService;

  const mockNoticeResponse: NoticeResponseTo = {
    id: BigInt(1),
    content: 'Test notice content',
    articleId: BigInt(1),
  };

  const mockNoticeRequest: NoticeRequestTo = {
    content: 'Test notice content',
    articleId: BigInt(1),
  };

  const mockNoticesService = {
    createNotice: jest.fn(),
    getAll: jest.fn(),
    getNotice: jest.fn(),
    updateNotice: jest.fn(),
    deleteNotice: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [NoticesController],
      providers: [
        {
          provide: NoticesService,
          useValue: mockNoticesService,
        },
      ],
    }).compile();

    controller = module.get<NoticesController>(NoticesController);
    service = module.get<NoticesService>(NoticesService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('createNotice', () => {
    it('should create a new notice successfully', async () => {
      mockNoticesService.createNotice.mockResolvedValue(mockNoticeResponse);

      const result = await controller.createNotice(mockNoticeRequest);

      expect(result).toEqual(mockNoticeResponse);
      expect(service.createNotice).toHaveBeenCalledWith(mockNoticeRequest);
      expect(service.createNotice).toHaveBeenCalledTimes(1);
    });

    it('should throw error when article not found', async () => {
      mockNoticesService.createNotice.mockRejectedValue(
        new NotFoundException('Article not found'),
      );

      await expect(controller.createNotice(mockNoticeRequest)).rejects.toThrow(
        NotFoundException,
      );
      expect(service.createNotice).toHaveBeenCalledWith(mockNoticeRequest);
    });
  });

  describe('getAllNotices', () => {
    it('should return an array of notices', async () => {
      const notices = [mockNoticeResponse, { ...mockNoticeResponse, id: 2 }];
      mockNoticesService.getAll.mockResolvedValue(notices);

      const result = await controller.getAllNotices();

      expect(result).toEqual(notices);
      expect(service.getAll).toHaveBeenCalledTimes(1);
    });

    it('should return empty array when no notices exist', async () => {
      mockNoticesService.getAll.mockResolvedValue([]);

      const result = await controller.getAllNotices();

      expect(result).toEqual([]);
      expect(service.getAll).toHaveBeenCalledTimes(1);
    });
  });

  describe('getNotice', () => {
    it('should return a notice by id', async () => {
      mockNoticesService.getNotice.mockResolvedValue(mockNoticeResponse);

      const result = await controller.getNotice(1);

      expect(result).toEqual(mockNoticeResponse);
      expect(service.getNotice).toHaveBeenCalledWith(1);
      expect(service.getNotice).toHaveBeenCalledTimes(1);
    });

    it('should throw NotFoundException when notice not found', async () => {
      mockNoticesService.getNotice.mockRejectedValue(
        new NotFoundException('Notice not found'),
      );

      await expect(controller.getNotice(999)).rejects.toThrow(
        NotFoundException,
      );
      expect(service.getNotice).toHaveBeenCalledWith(999);
    });
  });

  describe('updateNotice', () => {
    it('should update a notice successfully', async () => {
      const updateData: NoticeRequestTo = {
        content: 'Updated notice content',
        articleId: BigInt(1),
      };
      const updatedNotice = { ...mockNoticeResponse, ...updateData };

      mockNoticesService.updateNotice.mockResolvedValue(updatedNotice);

      const result = await controller.updateNotice(1, updateData);

      expect(result).toEqual(updatedNotice);
      expect(service.updateNotice).toHaveBeenCalledWith(1, updateData);
      expect(service.updateNotice).toHaveBeenCalledTimes(1);
    });

    it('should throw NotFoundException when notice not found', async () => {
      mockNoticesService.updateNotice.mockRejectedValue(
        new NotFoundException('Notice not found'),
      );

      await expect(
        controller.updateNotice(999, mockNoticeRequest),
      ).rejects.toThrow(NotFoundException);
    });

    it('should throw NotFoundException when article not found', async () => {
      mockNoticesService.updateNotice.mockRejectedValue(
        new NotFoundException('Article not found'),
      );

      await expect(
        controller.updateNotice(1, {
          ...mockNoticeRequest,
          articleId: BigInt(999),
        }),
      ).rejects.toThrow(NotFoundException);
    });
  });

  describe('deleteNotice', () => {
    it('should delete a notice successfully', async () => {
      mockNoticesService.deleteNotice.mockResolvedValue(undefined);

      const result = await controller.deleteNotice(1);

      expect(result).toBeUndefined();
      expect(service.deleteNotice).toHaveBeenCalledWith(1);
      expect(service.deleteNotice).toHaveBeenCalledTimes(1);
    });

    it('should throw NotFoundException when notice not found', async () => {
      mockNoticesService.deleteNotice.mockRejectedValue(
        new NotFoundException('Notice not found'),
      );

      await expect(controller.deleteNotice(999)).rejects.toThrow(
        NotFoundException,
      );
      expect(service.deleteNotice).toHaveBeenCalledWith(999);
    });
  });
});
