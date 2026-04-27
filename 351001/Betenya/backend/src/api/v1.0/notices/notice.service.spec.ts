import { Test, TestingModule } from '@nestjs/testing';
import {
  NotFoundException,
  InternalServerErrorException,
} from '@nestjs/common';
import { NoticesService } from './notices.service';
import { PrismaService } from '../../../services/prisma.service';
import { KafkaService } from '../../../kafka/kafka.service';
import { NoticeRequestTo } from '../../../dto/notices/NoticeRequestTo.dto';
import { RedisService } from '../../../redis/redis.service';

// Helpers to build mock fetch responses
const okJson = (body: unknown) =>
  Promise.resolve({ ok: true, status: 200, json: () => Promise.resolve(body) } as Response);

const notFoundResp = () =>
  Promise.resolve({ ok: false, status: 404, json: () => Promise.resolve({}) } as Response);

const serverErrorResp = () =>
  Promise.resolve({ ok: false, status: 500, json: () => Promise.resolve({}) } as Response);

describe('NoticesService (proxy to discussion)', () => {
  let service: NoticesService;
  let mockFetch: jest.SpyInstance;

  const mockArticle = { id: BigInt(1), title: 'Test', content: 'Content', userId: BigInt(1) };
  const noticeRaw = { id: 1, content: 'Test notice content', articleId: 1, state: 'APPROVE' };

  const mockNoticeRequest: NoticeRequestTo = {
    content: 'Test notice content',
    articleId: BigInt(1),
  };

  const mockPrismaService = {
    article: {
      findUnique: jest.fn(),
    },
  };

  const mockKafkaService = {
    isReady: jest.fn(),
    sendAndWait: jest.fn(),
  };

  const mockRedisService = {
    get: jest.fn().mockResolvedValue(null),
    set: jest.fn().mockResolvedValue(undefined),
    del: jest.fn().mockResolvedValue(undefined),
    delByPattern: jest.fn().mockResolvedValue(undefined),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        NoticesService,
        { provide: PrismaService, useValue: mockPrismaService },
        { provide: KafkaService, useValue: mockKafkaService },
        { provide: RedisService, useValue: mockRedisService },
      ],
    }).compile();

    service = module.get<NoticesService>(NoticesService);
    mockFetch = jest.spyOn(global, 'fetch' as any);
    jest.clearAllMocks();
  });

  afterEach(() => {
    mockFetch.mockRestore();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  // ---- createNotice (Kafka path) ---- //
  describe('createNotice (Kafka)', () => {
    it('creates via Kafka when ready', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockKafkaService.isReady.mockReturnValue(true);
      mockKafkaService.sendAndWait.mockResolvedValue(noticeRaw);

      const result = await service.createNotice(mockNoticeRequest);

      expect(result.id).toBe(BigInt(1));
      expect(result.state).toBe('APPROVE');
      expect(mockKafkaService.sendAndWait).toHaveBeenCalledWith(
        'CREATE',
        { content: 'Test notice content', articleId: 1 },
        '1',
      );
      expect(mockFetch).not.toHaveBeenCalled();
    });

    it('throws NotFoundException when article not found', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(null);

      await expect(service.createNotice(mockNoticeRequest)).rejects.toThrow(NotFoundException);
      expect(mockKafkaService.sendAndWait).not.toHaveBeenCalled();
    });
  });

  // ---- createNotice (HTTP fallback) ---- //
  describe('createNotice (HTTP fallback)', () => {
    it('creates via HTTP when Kafka not ready', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockKafkaService.isReady.mockReturnValue(false);
      mockFetch.mockReturnValue(okJson(noticeRaw));

      const result = await service.createNotice(mockNoticeRequest);

      expect(result.id).toBe(BigInt(1));
      expect(mockFetch).toHaveBeenCalled();
    });

    it('throws InternalServerErrorException when discussion service fails', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockKafkaService.isReady.mockReturnValue(false);
      mockFetch.mockReturnValue(serverErrorResp());

      await expect(service.createNotice(mockNoticeRequest)).rejects.toThrow(
        InternalServerErrorException,
      );
    });
  });

  // ---- getAll ---- //
  describe('getAll', () => {
    it('returns array of notices', async () => {
      const list = [noticeRaw, { ...noticeRaw, id: 2 }];
      mockFetch.mockReturnValue(okJson(list));

      const result = await service.getAll();

      expect(result).toHaveLength(2);
      expect(result[0].id).toBe(BigInt(1));
      expect(result[0].state).toBe('APPROVE');
    });

    it('returns empty array when no notices', async () => {
      mockFetch.mockReturnValue(okJson([]));

      expect(await service.getAll()).toEqual([]);
    });

    it('throws InternalServerErrorException when discussion service fails', async () => {
      mockFetch.mockReturnValue(serverErrorResp());

      await expect(service.getAll()).rejects.toThrow(InternalServerErrorException);
    });
  });

  // ---- getNotice ---- //
  describe('getNotice', () => {
    it('returns notice by id', async () => {
      mockFetch.mockReturnValue(okJson(noticeRaw));

      const result = await service.getNotice(1);

      expect(result.id).toBe(BigInt(1));
      expect(result.state).toBe('APPROVE');
    });

    it('throws NotFoundException when discussion returns 404', async () => {
      mockFetch.mockReturnValue(notFoundResp());

      await expect(service.getNotice(999)).rejects.toThrow(NotFoundException);
    });

    it('throws InternalServerErrorException on other error', async () => {
      mockFetch.mockReturnValue(serverErrorResp());

      await expect(service.getNotice(1)).rejects.toThrow(InternalServerErrorException);
    });
  });

  // ---- updateNotice ---- //
  describe('updateNotice', () => {
    const updateDto: NoticeRequestTo = { content: 'Updated', articleId: BigInt(1) };

    it('updates when article exists and notice exists', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockFetch.mockReturnValue(okJson({ ...noticeRaw, content: 'Updated' }));

      const result = await service.updateNotice(1, updateDto);

      expect(result.content).toBe('Updated');
    });

    it('throws NotFoundException when article not found', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(null);

      await expect(service.updateNotice(1, updateDto)).rejects.toThrow(NotFoundException);
      expect(mockFetch).not.toHaveBeenCalled();
    });

    it('throws NotFoundException when discussion returns 404', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockFetch.mockReturnValue(notFoundResp());

      await expect(service.updateNotice(999, updateDto)).rejects.toThrow(NotFoundException);
    });

    it('throws InternalServerErrorException on other discussion error', async () => {
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockFetch.mockReturnValue(serverErrorResp());

      await expect(service.updateNotice(1, updateDto)).rejects.toThrow(
        InternalServerErrorException,
      );
    });
  });

  // ---- deleteNotice ---- //
  describe('deleteNotice', () => {
    it('deletes successfully', async () => {
      mockFetch.mockReturnValue(
        Promise.resolve({ ok: true, status: 204 } as Response),
      );

      await expect(service.deleteNotice(1)).resolves.toBeUndefined();
    });

    it('throws NotFoundException when discussion returns 404', async () => {
      mockFetch.mockReturnValue(notFoundResp());

      await expect(service.deleteNotice(999)).rejects.toThrow(NotFoundException);
    });

    it('throws InternalServerErrorException on other error', async () => {
      mockFetch.mockReturnValue(serverErrorResp());

      await expect(service.deleteNotice(1)).rejects.toThrow(InternalServerErrorException);
    });
  });

  // ---- edge cases ---- //
  describe('edge cases', () => {
    it('handles special characters in content', async () => {
      const special = 'Hello !@#$%^&*() characters';
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockKafkaService.isReady.mockReturnValue(true);
      mockKafkaService.sendAndWait.mockResolvedValue({ ...noticeRaw, content: special });

      const result = await service.createNotice({ content: special, articleId: BigInt(1) });

      expect(result.content).toBe(special);
    });

    it('handles long content (1000 chars)', async () => {
      const longContent = 'a'.repeat(1000);
      mockPrismaService.article.findUnique.mockResolvedValue(mockArticle);
      mockKafkaService.isReady.mockReturnValue(true);
      mockKafkaService.sendAndWait.mockResolvedValue({ ...noticeRaw, content: longContent });

      const result = await service.createNotice({ content: longContent, articleId: BigInt(1) });

      expect(result.content.length).toBe(1000);
    });
  });
});
