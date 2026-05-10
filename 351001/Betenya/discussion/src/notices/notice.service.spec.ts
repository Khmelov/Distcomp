import { Test, TestingModule } from '@nestjs/testing';
import { NotFoundException, InternalServerErrorException } from '@nestjs/common';
import { types } from 'cassandra-driver';
import { NoticesService } from './notices.service';
import { CassandraService } from '../cassandra/cassandra.service';
import { NoticeRequestTo } from '../dto/NoticeRequestTo.dto';

const long = (n: number) => types.Long.fromNumber(n);

/** Build a fake cassandra-driver result row */
const makeRow = (id: number, articleId: number, content: string, state = 'PENDING') =>
  ({ id: long(id), article_id: long(articleId), content, state } as unknown as types.Row);

const makeResult = (...rows: types.Row[]) => ({ rows } as types.ResultSet);
const emptyResult = () => makeResult();

describe('NoticesService (Cassandra)', () => {
  let service: NoticesService;
  let execute: jest.Mock;

  beforeEach(async () => {
    execute = jest.fn();

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        NoticesService,
        {
          provide: CassandraService,
          useValue: { client: { execute } },
        },
      ],
    }).compile();

    service = module.get<NoticesService>(NoticesService);
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('createNotice', () => {
    const dto: NoticeRequestTo = { content: 'Hello world', articleId: 5 };

    it('creates a notice and returns response DTO', async () => {
      execute
        .mockResolvedValueOnce(emptyResult())
        .mockResolvedValueOnce(makeResult({ value: long(1) } as any))
        .mockResolvedValueOnce(emptyResult());

      const result = await service.createNotice(dto);

      expect(result).toEqual({ id: 1, content: 'Hello world', articleId: 5, state: 'PENDING' });
      expect(execute).toHaveBeenCalledTimes(3);
    });

    it('creates a notice with custom state', async () => {
      execute
        .mockResolvedValueOnce(emptyResult())
        .mockResolvedValueOnce(makeResult({ value: long(1) } as any))
        .mockResolvedValueOnce(emptyResult());

      const result = await service.createNotice(dto, 'APPROVE');

      expect(result.state).toBe('APPROVE');
    });

    it('propagates errors from the counter update', async () => {
      execute.mockRejectedValueOnce(new Error('Counter error'));

      await expect(service.createNotice(dto)).rejects.toThrow('Counter error');
    });

    it('propagates errors from the insert', async () => {
      execute
        .mockResolvedValueOnce(emptyResult())
        .mockResolvedValueOnce(makeResult({ value: long(2) } as any))
        .mockRejectedValueOnce(new Error('Insert error'));

      await expect(service.createNotice(dto)).rejects.toThrow('Insert error');
    });
  });

  describe('getAll', () => {
    it('returns all notices', async () => {
      execute.mockResolvedValueOnce(
        makeResult(makeRow(1, 1, 'First', 'APPROVE'), makeRow(2, 1, 'Second', 'PENDING')),
      );

      const result = await service.getAll();

      expect(result).toHaveLength(2);
      expect(result[0]).toEqual({ id: 1, content: 'First', articleId: 1, state: 'APPROVE' });
      expect(result[1]).toEqual({ id: 2, content: 'Second', articleId: 1, state: 'PENDING' });
    });

    it('returns empty array when no notices', async () => {
      execute.mockResolvedValueOnce(emptyResult());

      expect(await service.getAll()).toEqual([]);
    });
  });

  describe('getNotice', () => {
    it('returns notice by id', async () => {
      execute.mockResolvedValueOnce(makeResult(makeRow(1, 2, 'Content', 'APPROVE')));

      const result = await service.getNotice(1);

      expect(result).toEqual({ id: 1, content: 'Content', articleId: 2, state: 'APPROVE' });
    });

    it('throws NotFoundException when not found', async () => {
      execute.mockResolvedValueOnce(emptyResult());

      await expect(service.getNotice(999)).rejects.toThrow(NotFoundException);
    });
  });

  describe('updateNotice', () => {
    const dto: NoticeRequestTo = { content: 'Updated', articleId: 3 };

    it('updates and returns updated DTO', async () => {
      execute
        .mockResolvedValueOnce(makeResult(makeRow(1, 1, 'Old', 'APPROVE')))
        .mockResolvedValueOnce(emptyResult());

      const result = await service.updateNotice(1, dto);

      expect(result).toEqual({ id: 1, content: 'Updated', articleId: 3, state: 'APPROVE' });
      expect(execute).toHaveBeenCalledTimes(2);
    });

    it('throws NotFoundException when notice does not exist', async () => {
      execute.mockResolvedValueOnce(emptyResult());

      await expect(service.updateNotice(999, dto)).rejects.toThrow(NotFoundException);
      expect(execute).toHaveBeenCalledTimes(1);
    });

    it('throws InternalServerErrorException on DB error during update', async () => {
      execute
        .mockResolvedValueOnce(makeResult(makeRow(1, 1, 'Old')))
        .mockRejectedValueOnce(new Error('DB failure'));

      await expect(service.updateNotice(1, dto)).rejects.toThrow(
        InternalServerErrorException,
      );
    });
  });

  describe('updateNoticeState', () => {
    it('updates the state column', async () => {
      execute.mockResolvedValueOnce(emptyResult());

      await service.updateNoticeState(1, 'APPROVE');

      expect(execute).toHaveBeenCalledWith(
        expect.stringContaining('UPDATE'),
        ['APPROVE', long(1)],
        { prepare: true },
      );
    });
  });

  describe('deleteNotice', () => {
    it('deletes notice successfully', async () => {
      execute
        .mockResolvedValueOnce(makeResult(makeRow(1, 1, 'Content')))
        .mockResolvedValueOnce(emptyResult());

      await expect(service.deleteNotice(1)).resolves.toBeUndefined();
      expect(execute).toHaveBeenCalledTimes(2);
    });

    it('throws NotFoundException when notice does not exist', async () => {
      execute.mockResolvedValueOnce(emptyResult());

      await expect(service.deleteNotice(999)).rejects.toThrow(NotFoundException);
      expect(execute).toHaveBeenCalledTimes(1);
    });

    it('propagates DB errors on delete', async () => {
      execute
        .mockResolvedValueOnce(makeResult(makeRow(1, 1, 'Content')))
        .mockRejectedValueOnce(new Error('Delete error'));

      await expect(service.deleteNotice(1)).rejects.toThrow('Delete error');
    });
  });

  describe('edge cases', () => {
    it('handles special characters in content', async () => {
      const special = 'Hello !@#$%^&*()';
      execute.mockResolvedValueOnce(makeResult(makeRow(1, 1, special)));

      const result = await service.getNotice(1);

      expect(result.content).toBe(special);
    });

    it('handles long content (2048 chars)', async () => {
      const longContent = 'a'.repeat(2048);
      execute
        .mockResolvedValueOnce(emptyResult())
        .mockResolvedValueOnce(makeResult({ value: long(1) } as any))
        .mockResolvedValueOnce(emptyResult());

      const result = await service.createNotice({ content: longContent, articleId: 1 });

      expect(result.content.length).toBe(2048);
    });
  });
});
