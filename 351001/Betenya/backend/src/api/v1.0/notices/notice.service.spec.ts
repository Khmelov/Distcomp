import { Test, TestingModule } from '@nestjs/testing';
import {
  NotFoundException,
  InternalServerErrorException,
} from '@nestjs/common';
import { NoticesService } from './notices.service';
import { PrismaService } from '../../../services/prisma.service';
import { NoticeRequestTo } from '../../../dto/notices/NoticeRequestTo.dto';

describe('NoticesService', () => {
  let service: NoticesService;

  const mockArticle = {
    id: 1,
    title: 'Test Article',
    content: 'Test Content',
    userId: 1,
  };

  const mockNotice = {
    id: 1,
    content: 'Test notice content',
    articleId: 1,
  };

  const mockNoticeRequest: NoticeRequestTo = {
    content: 'Test notice content',
    articleId: BigInt(1),
  };

  const mockPrismaService = {
    article: {
      findUnique: jest.fn(),
    },
    notice: {
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
        NoticesService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
      ],
    }).compile();

    service = module.get<NoticesService>(NoticesService);

    // Reset all mocks
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('createNotice', () => {
    it('should create a new notice successfully', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockPrismaService.notice.create.mockResolvedValue(mockNotice);

      const result = await service.createNotice(mockNoticeRequest);

      expect(result).toEqual(mockNotice);
      expect(mockPrismaService.article.findUnique).toHaveBeenCalledWith({
        where: { id: mockNoticeRequest.articleId },
      });
      expect(mockPrismaService.notice.create).toHaveBeenCalledWith({
        data: mockNoticeRequest,
      });
    });

    it('should throw NotFoundException if article not found', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(null);

      await expect(service.createNotice(mockNoticeRequest)).rejects.toThrow(
        new NotFoundException('Article not found'),
      );
      expect(mockPrismaService.article.findUnique).toHaveBeenCalledWith({
        where: { id: mockNoticeRequest.articleId },
      });
      expect(mockPrismaService.notice.create).not.toHaveBeenCalled();
    });

    it('should propagate database errors', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockPrismaService.notice.create.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(service.createNotice(mockNoticeRequest)).rejects.toThrow(
        'Database error',
      );
    });
  });

  describe('getAll', () => {
    it('should return all notices', async () => {
      const notices = [
        mockNotice,
        { ...mockNotice, id: 2, content: 'Another notice' },
      ];
      mockPrismaService.notice.findMany.mockResolvedValue(notices);

      const result = await service.getAll();

      expect(result).toEqual(notices);
      expect(mockPrismaService.notice.findMany).toHaveBeenCalledTimes(1);
    });

    it('should return empty array if no notices exist', async () => {
      mockPrismaService.notice.findMany.mockResolvedValue([]);

      const result = await service.getAll();

      expect(result).toEqual([]);
      expect(mockPrismaService.notice.findMany).toHaveBeenCalledTimes(1);
    });
  });

  describe('getNotice', () => {
    it('should return a notice by id', async () => {
      mockPrismaService.notice.findUnique.mockResolvedValue(mockNotice);

      const result = await service.getNotice(1);

      expect(result).toEqual(mockNotice);
      expect(mockPrismaService.notice.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should throw NotFoundException if notice not found', async () => {
      mockPrismaService.notice.findUnique.mockResolvedValue(null);

      await expect(service.getNotice(999)).rejects.toThrow(
        new NotFoundException('Notice not found'),
      );
      expect(mockPrismaService.notice.findUnique).toHaveBeenCalledWith({
        where: { id: 999 },
      });
    });
  });

  describe('updateNotice', () => {
    it('should update a notice successfully', async () => {
      const updateData: NoticeRequestTo = {
        content: 'Updated notice content',
        articleId: BigInt(1),
      };
      const updatedNotice = { ...mockNotice, ...updateData };

      mockPrismaService.notice.findUnique.mockResolvedValueOnce(mockNotice); // для проверки существования уведомления
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle); // для проверки существования статьи
      mockPrismaService.notice.update.mockResolvedValue(updatedNotice);

      const result = await service.updateNotice(1, updateData);

      expect(result).toEqual(updatedNotice);
      expect(mockPrismaService.notice.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(mockPrismaService.article.findUnique).toHaveBeenCalledWith({
        where: { id: updateData.articleId },
      });
      expect(mockPrismaService.notice.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: updateData,
      });
    });

    it('should throw NotFoundException if notice not found', async () => {
      mockPrismaService.notice.findUnique.mockResolvedValue(null);

      await expect(
        service.updateNotice(999, mockNoticeRequest),
      ).rejects.toThrow(new NotFoundException('Notice not found'));
      expect(mockPrismaService.notice.update).not.toHaveBeenCalled();
    });

    it('should throw NotFoundException if article not found', async () => {
      mockPrismaService.notice.findUnique.mockResolvedValue(mockNotice);
      mockPrismaService.article.findUnique.mockResolvedValue(null);

      await expect(service.updateNotice(1, mockNoticeRequest)).rejects.toThrow(
        new NotFoundException('Article not found'),
      );
      expect(mockPrismaService.article.findUnique).toHaveBeenCalledWith({
        where: { id: mockNoticeRequest.articleId },
      });
      expect(mockPrismaService.notice.update).not.toHaveBeenCalled();
    });

    it('should throw InternalServerErrorException on database error', async () => {
      mockPrismaService.notice.findUnique.mockResolvedValue(mockNotice);
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockPrismaService.notice.update.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(service.updateNotice(1, mockNoticeRequest)).rejects.toThrow(
        InternalServerErrorException,
      );
    });
  });

  describe('deleteNotice', () => {
    it('should delete a notice successfully', async () => {
      mockPrismaService.notice.findUnique.mockResolvedValue(mockNotice);
      mockPrismaService.notice.delete.mockResolvedValue(mockNotice);

      await service.deleteNotice(1);

      expect(mockPrismaService.notice.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(mockPrismaService.notice.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should throw NotFoundException if notice not found', async () => {
      mockPrismaService.notice.findUnique.mockResolvedValue(null);

      await expect(service.deleteNotice(999)).rejects.toThrow(
        new NotFoundException('Notice not found'),
      );
      expect(mockPrismaService.notice.findUnique).toHaveBeenCalledWith({
        where: { id: 999 },
      });
      expect(mockPrismaService.notice.delete).not.toHaveBeenCalled();
    });

    it('should propagate database errors on delete', async () => {
      mockPrismaService.notice.findUnique.mockResolvedValue(mockNotice);
      mockPrismaService.notice.delete.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(service.deleteNotice(1)).rejects.toThrow('Database error');
    });
  });

  // Дополнительные тесты для граничных случаев
  describe('edge cases', () => {
    it('should handle empty content in notice creation', async () => {
      const noticeWithEmptyContent: NoticeRequestTo = {
        content: '',
        articleId: BigInt(1),
      };

      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockPrismaService.notice.create.mockResolvedValue({
        ...mockNotice,
        content: '',
      });

      const result = await service.createNotice(noticeWithEmptyContent);

      expect(result.content).toBe('');
      expect(mockPrismaService.notice.create).toHaveBeenCalledWith({
        data: noticeWithEmptyContent,
      });
    });

    it('should handle very long content in notice', async () => {
      const longContent = 'a'.repeat(1000);
      const noticeWithLongContent: NoticeRequestTo = {
        content: longContent,
        articleId: BigInt(1),
      };

      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockPrismaService.notice.create.mockResolvedValue({
        ...mockNotice,
        content: longContent,
      });

      const result = await service.createNotice(noticeWithLongContent);

      expect(result.content).toBe(longContent);
      expect(result.content.length).toBe(1000);
    });

    it('should find notice with special characters in content', async () => {
      const specialContent = 'Special !@#$%^&*() characters';
      const noticeWithSpecialChars = { ...mockNotice, content: specialContent };

      mockPrismaService.notice.findUnique.mockResolvedValue(
        noticeWithSpecialChars,
      );

      const result = await service.getNotice(1);

      expect(result.content).toBe(specialContent);
    });
  });
});
