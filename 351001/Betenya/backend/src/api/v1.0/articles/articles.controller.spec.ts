jest.mock('@prisma/client');

import { Test, TestingModule } from '@nestjs/testing';
import { ArticlesController } from './articles.controller';
import { ArticlesService } from './articles.service';
import { ArticleRequestTo } from '../../../dto/articles/ArticleRequestTo.dto';
import { ArticleResponseTo } from '../../../dto/articles/ArticleResponseTo.dto';

describe('ArticlesController', () => {
  let controller: ArticlesController;
  let service: ArticlesService;

  const mockArticleResponse: ArticleResponseTo = {
    id: BigInt(1),
    title: 'Test Article',
    content: 'Test Content',
    userId: BigInt(1),
    created: new Date(),
    modified: new Date(),
  };

  const mockArticleRequest: ArticleRequestTo = {
    title: 'Test Article',
    content: 'Test Content',
    userId: BigInt(1),
    stickers: ['test1', 'test2'],
  };

  const mockArticlesService = {
    createArticle: jest.fn(),
    getAll: jest.fn(),
    getArticleById: jest.fn(),
    updateArticle: jest.fn(),
    deleteArticle: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [ArticlesController],
      providers: [
        {
          provide: ArticlesService,
          useValue: mockArticlesService,
        },
      ],
    }).compile();

    controller = module.get<ArticlesController>(ArticlesController);
    service = module.get<ArticlesService>(ArticlesService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('createArticle', () => {
    it('should create a new article', async () => {
      mockArticlesService.createArticle.mockResolvedValue(mockArticleResponse);

      const result = await controller.createArticle(mockArticleRequest);

      expect(result).toEqual(mockArticleResponse);
      expect(service.createArticle).toHaveBeenCalledWith(mockArticleRequest);
      expect(service.createArticle).toHaveBeenCalledTimes(1);
    });
  });

  describe('getAllArticles', () => {
    it('should return an array of articles', async () => {
      const articles = [mockArticleResponse];
      mockArticlesService.getAll.mockResolvedValue(articles);

      const result = await controller.getAllArticles();

      expect(result).toEqual(articles);
      expect(service.getAll).toHaveBeenCalledTimes(1);
    });

    it('should return empty array when no articles exist', async () => {
      mockArticlesService.getAll.mockResolvedValue([]);

      const result = await controller.getAllArticles();

      expect(result).toEqual([]);
      expect(service.getAll).toHaveBeenCalledTimes(1);
    });
  });

  describe('getArticle', () => {
    it('should return an article by id', async () => {
      mockArticlesService.getArticleById.mockResolvedValue(mockArticleResponse);

      const result = await controller.getArticle(1);

      expect(result).toEqual(mockArticleResponse);
      expect(service.getArticleById).toHaveBeenCalledWith(1);
      expect(service.getArticleById).toHaveBeenCalledTimes(1);
    });
  });

  describe('updateArticle', () => {
    it('should update an article', async () => {
      const updateData: ArticleRequestTo = {
        title: 'Updated Title',
        content: 'Updated Content',
        userId: 1,
      };
      const updatedArticle = { ...mockArticleResponse, ...updateData };

      mockArticlesService.updateArticle.mockResolvedValue(updatedArticle);

      const result = await controller.updateArticle(1, updateData);

      expect(result).toEqual(updatedArticle);
      expect(service.updateArticle).toHaveBeenCalledWith(1, updateData);
      expect(service.updateArticle).toHaveBeenCalledTimes(1);
    });
  });

  describe('deleteArticle', () => {
    it('should delete an article', async () => {
      mockArticlesService.deleteArticle.mockResolvedValue(undefined);

      const result = await controller.deleteArticle(1);

      expect(result).toBeUndefined();
      expect(service.deleteArticle).toHaveBeenCalledWith(1);
      expect(service.deleteArticle).toHaveBeenCalledTimes(1);
    });
  });
});
