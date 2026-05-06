import { Test, TestingModule } from '@nestjs/testing';
import {
  ForbiddenException,
  NotFoundException,
  UnauthorizedException,
  InternalServerErrorException,
} from '@nestjs/common';
import { ArticlesService } from './articles.service';
import { PrismaService } from '../../../services/prisma.service';
import { ArticleRequestTo } from '../../../dto/articles/ArticleRequestTo.dto';

describe('ArticlesService', () => {
  let service: ArticlesService;
  let prismaService = new PrismaService();

  const mockUser = {
    id: 1,
    login: 'testuser',
    password: 'hashedpassword',
    firstName: 'Test',
    lastName: 'User',
  };

  const mockArticle = {
    id: 1,
    title: 'Test Article',
    content: 'Test Content',
    userId: 1,
  };

  const mockSticker = {
    id: 1,
    name: 'test1',
  };

  const mockArticleRequest: ArticleRequestTo = {
    title: 'Test Article',
    content: 'Test Content',
    userId: BigInt(1),
    stickers: ['test1', 'test2'],
  };

  const mockPrismaService = {
    user: {
      findUnique: jest.fn(),
    },
    article: {
      findUnique: jest.fn(),
      findMany: jest.fn(),
      create: jest.fn(),
      update: jest.fn(),
      delete: jest.fn(),
    },
    sticker: {
      findFirst: jest.fn(),
      create: jest.fn(),
      delete: jest.fn(),
    },
    articleSticker: {
      createMany: jest.fn(),
      findMany: jest.fn(),
      deleteMany: jest.fn(),
      count: jest.fn(),
    },
    $transaction: jest.fn((callback) => callback(mockPrismaService)),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ArticlesService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
      ],
    }).compile();

    service = module.get<ArticlesService>(ArticlesService);
    prismaService = module.get<PrismaService>(PrismaService);

    // Reset all mocks
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('createArticle', () => {
    it('should create a new article with stickers successfully', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);
      mockPrismaService.article.findUnique.mockResolvedValue(null);
      mockPrismaService.sticker.findFirst
        .mockResolvedValueOnce(mockSticker) // First sticker exists
        .mockResolvedValueOnce(null); // Second sticker doesn't exist
      mockPrismaService.sticker.create.mockResolvedValue({
        id: 2,
        name: 'test2',
      });
      mockPrismaService.article.create.mockResolvedValue(mockArticle);
      mockPrismaService.articleSticker.createMany.mockResolvedValue({
        count: 2,
      });

      const result = await service.createArticle(mockArticleRequest);

      expect(result).toEqual(mockArticle);
      expect(mockPrismaService.user.findUnique).toHaveBeenCalledWith({
        where: { id: mockArticleRequest.userId },
      });
      expect(mockPrismaService.article.findUnique).toHaveBeenCalledWith({
        where: { title: mockArticleRequest.title },
      });
      expect(mockPrismaService.$transaction).toHaveBeenCalled();
    });

    it('should create an article without stickers', async () => {
      const articleWithoutStickers: ArticleRequestTo = {
        title: 'Test Article',
        content: 'Test Content',
        userId: 1,
      };

      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);
      mockPrismaService.article.findUnique.mockResolvedValue(null);
      mockPrismaService.article.create.mockResolvedValue(mockArticle);

      const result = await service.createArticle(articleWithoutStickers);

      expect(result).toEqual(mockArticle);
      expect(
        mockPrismaService.articleSticker.createMany,
      ).not.toHaveBeenCalled();
    });

    it('should throw UnauthorizedException if user not found', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(null);

      await expect(service.createArticle(mockArticleRequest)).rejects.toThrow(
        UnauthorizedException,
      );
      expect(mockPrismaService.user.findUnique).toHaveBeenCalledWith({
        where: { id: mockArticleRequest.userId },
      });
    });

    it('should throw ForbiddenException if article title already exists', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);

      await expect(service.createArticle(mockArticleRequest)).rejects.toThrow(
        ForbiddenException,
      );
    });
  });

  describe('getAll', () => {
    it('should return all articles', async () => {
      const articles = [mockArticle, { ...mockArticle, id: 2 }];
      mockPrismaService.article.findMany.mockResolvedValue(articles);

      const result = await service.getAll();

      expect(result).toEqual(articles);
      expect(mockPrismaService.article.findMany).toHaveBeenCalledTimes(1);
    });

    it('should return empty array if no articles exist', async () => {
      mockPrismaService.article.findMany.mockResolvedValue([]);

      const result = await service.getAll();

      expect(result).toEqual([]);
      expect(mockPrismaService.article.findMany).toHaveBeenCalledTimes(1);
    });
  });

  describe('getArticleById', () => {
    it('should return an article by id', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);

      const result = await service.getArticleById(1);

      expect(result).toEqual(mockArticle);
      expect(mockPrismaService.article.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should throw UnauthorizedException if article not found', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(null);

      await expect(service.getArticleById(999)).rejects.toThrow(
        UnauthorizedException,
      );
      expect(mockPrismaService.article.findUnique).toHaveBeenCalledWith({
        where: { id: 999 },
      });
    });
  });

  describe('updateArticle', () => {
    it('should update an article successfully', async () => {
      const updateData: ArticleRequestTo = {
        title: 'Updated Title',
        content: 'Updated Content',
        userId: 1,
      };
      const updatedArticle = { ...mockArticle, ...updateData };

      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockPrismaService.article.update.mockResolvedValue(updatedArticle);

      const result = await service.updateArticle(1, updateData);

      expect(result).toEqual(updatedArticle);
      expect(mockPrismaService.article.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(mockPrismaService.article.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: updateData,
      });
    });

    it('should throw UnauthorizedException if article not found', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(null);

      await expect(
        service.updateArticle(999, mockArticleRequest),
      ).rejects.toThrow(UnauthorizedException);
      expect(mockPrismaService.article.update).not.toHaveBeenCalled();
    });

    it('should throw InternalServerErrorException on database error', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockPrismaService.article.update.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(
        service.updateArticle(1, mockArticleRequest),
      ).rejects.toThrow(InternalServerErrorException);
    });
  });

  describe('deleteArticle', () => {
    it('should delete an article and orphaned stickers successfully', async () => {
      const articleStickers = [
        {
          articleId: 1,
          stickerId: 1,
          sticker: mockSticker,
        },
      ];

      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockPrismaService.articleSticker.findMany.mockResolvedValue(
        articleStickers,
      );
      mockPrismaService.articleSticker.count.mockResolvedValue(0); // No other connections
      mockPrismaService.sticker.delete.mockResolvedValue(mockSticker);
      mockPrismaService.article.delete.mockResolvedValue(mockArticle);

      await service.deleteArticle(1);

      expect(mockPrismaService.$transaction).toHaveBeenCalled();
      expect(mockPrismaService.articleSticker.findMany).toHaveBeenCalledWith({
        where: { articleId: 1 },
        include: { sticker: true },
      });
      expect(mockPrismaService.sticker.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should not delete stickers that are still in use', async () => {
      const articleStickers = [
        {
          articleId: 1,
          stickerId: 1,
          sticker: mockSticker,
        },
      ];

      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockPrismaService.articleSticker.findMany.mockResolvedValue(
        articleStickers,
      );
      mockPrismaService.articleSticker.count.mockResolvedValue(1); // Still has other connections
      mockPrismaService.article.delete.mockResolvedValue(mockArticle);

      await service.deleteArticle(1);

      expect(mockPrismaService.sticker.delete).not.toHaveBeenCalled();
    });

    it('should throw NotFoundException if article not found', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(null);

      await expect(service.deleteArticle(999)).rejects.toThrow(
        NotFoundException,
      );
      expect(mockPrismaService.$transaction).not.toHaveBeenCalled();
    });
  });
});
