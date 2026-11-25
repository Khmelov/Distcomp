import { Message } from '../../entities';
import {
  AppError,
  ConflictError,
  NotFoundError,
  UnprocessableEntityError,
  ValidationError,
} from '../../errors/AppError';
import { DiscussionMessageRepository } from '../DiscussionMessageRepository';

describe('DiscussionMessageRepository', () => {
  let repository: DiscussionMessageRepository;
  let fetchMock: jest.SpyInstance;

  const message: Message = {
    id: 100,
    topicId: 10,
    content: 'Test message',
  };

  const mockResponse = (status: number, body?: unknown): Response =>
    ({
      status,
      ok: status >= 200 && status < 300,
      json: jest.fn().mockResolvedValue(body),
    }) as unknown as Response;

  beforeEach(() => {
    repository = new DiscussionMessageRepository();
    fetchMock = jest.spyOn(global, 'fetch');
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('findAll', () => {
    it('should return all messages', async () => {
      fetchMock.mockResolvedValue(mockResponse(200, [message]));

      const result = await repository.findAll();

      expect(result).toEqual([message]);
      expect(fetchMock).toHaveBeenCalledWith(
        expect.stringContaining('/messages')
      );
    });

    it('should throw ValidationError on 400', async () => {
      fetchMock.mockResolvedValue(mockResponse(400));

      await expect(repository.findAll()).rejects.toBeInstanceOf(
        ValidationError
      );
    });

    it('should throw ConflictError on 403', async () => {
      fetchMock.mockResolvedValue(mockResponse(403));

      await expect(repository.findAll()).rejects.toBeInstanceOf(ConflictError);
    });

    it('should throw NotFoundError on 404', async () => {
      fetchMock.mockResolvedValue(mockResponse(404));

      await expect(repository.findAll()).rejects.toBeInstanceOf(NotFoundError);
    });

    it('should throw UnprocessableEntityError on 422', async () => {
      fetchMock.mockResolvedValue(mockResponse(422));

      await expect(repository.findAll()).rejects.toBeInstanceOf(
        UnprocessableEntityError
      );
    });

    it('should throw AppError on unexpected status', async () => {
      fetchMock.mockResolvedValue(mockResponse(500));

      await expect(repository.findAll()).rejects.toMatchObject({
        statusCode: 500,
        errorCode: '50001',
      });
    });
  });

  describe('findById', () => {
    it('should return a message by id', async () => {
      fetchMock.mockResolvedValue(mockResponse(200, message));

      const result = await repository.findById(100);

      expect(result).toEqual(message);
      expect(fetchMock).toHaveBeenCalledWith(
        expect.stringContaining('/messages/100')
      );
    });

    it('should return undefined if message does not exist', async () => {
      fetchMock.mockResolvedValue(mockResponse(404));

      const result = await repository.findById(100);

      expect(result).toBeUndefined();
    });

    it('should throw AppError on server error', async () => {
      fetchMock.mockResolvedValue(mockResponse(500));

      await expect(repository.findById(100)).rejects.toBeInstanceOf(AppError);
    });
  });

  describe('findByTopicId', () => {
    it('should return messages by topic id', async () => {
      fetchMock.mockResolvedValue(mockResponse(200, [message]));

      const result = await repository.findByTopicId(10);

      expect(result).toEqual([message]);
    });

    it('should pass sortOrder to Discussion', async () => {
      fetchMock.mockResolvedValue(mockResponse(200, [message]));

      await repository.findByTopicId(10, 'desc');

      expect(fetchMock).toHaveBeenCalledWith(
        expect.stringContaining('/by-topic/10?sortOrder=desc')
      );
    });

    it('should throw AppError on failed request', async () => {
      fetchMock.mockResolvedValue(mockResponse(500));

      await expect(repository.findByTopicId(10)).rejects.toBeInstanceOf(
        AppError
      );
    });
  });

  describe('save', () => {
    it('should create a message', async () => {
      fetchMock.mockResolvedValue(mockResponse(201, message));

      const { id, ...newMessage } = message;

      const result = await repository.save(newMessage);

      expect(result).toEqual(message);
      expect(fetchMock).toHaveBeenCalledWith(
        expect.stringContaining('/messages'),
        expect.objectContaining({
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(newMessage),
        })
      );
    });

    it('should throw AppError on failed request', async () => {
      fetchMock.mockResolvedValue(mockResponse(500));

      await expect(
        repository.save({
          topicId: 10,
          content: 'Test message',
        })
      ).rejects.toBeInstanceOf(AppError);
    });
  });

  describe('update', () => {
    it('should update a message', async () => {
      const updatedMessage = { ...message, content: 'Updated message' };

      fetchMock.mockResolvedValue(mockResponse(200, updatedMessage));

      const result = await repository.update(100, {
        content: 'Updated message',
      });

      expect(result).toEqual(updatedMessage);
      expect(fetchMock).toHaveBeenCalledWith(
        expect.stringContaining('/messages/100'),
        expect.objectContaining({
          method: 'PUT',
          body: JSON.stringify({ content: 'Updated message' }),
        })
      );
    });

    it('should return undefined if message does not exist', async () => {
      fetchMock.mockResolvedValue(mockResponse(404));

      const result = await repository.update(100, {
        content: 'Updated message',
      });

      expect(result).toBeUndefined();
    });

    it('should throw AppError on server error', async () => {
      fetchMock.mockResolvedValue(mockResponse(500));

      await expect(
        repository.update(100, { content: 'Updated message' })
      ).rejects.toBeInstanceOf(AppError);
    });
  });

  describe('delete', () => {
    it('should delete a message', async () => {
      fetchMock.mockResolvedValue(mockResponse(204));

      const result = await repository.delete(100);

      expect(result).toBe(true);
      expect(fetchMock).toHaveBeenCalledWith(
        expect.stringContaining('/messages/100'),
        { method: 'DELETE' }
      );
    });

    it('should return false if message does not exist', async () => {
      fetchMock.mockResolvedValue(mockResponse(404));

      const result = await repository.delete(100);

      expect(result).toBe(false);
    });

    it('should throw AppError on server error', async () => {
      fetchMock.mockResolvedValue(mockResponse(500));

      await expect(repository.delete(100)).rejects.toBeInstanceOf(AppError);
    });
  });

  describe('existsById', () => {
    it('should return true if message exists', async () => {
      fetchMock.mockResolvedValue(mockResponse(200, message));

      const result = await repository.existsById(100);

      expect(result).toBe(true);
    });

    it('should return false if message does not exist', async () => {
      fetchMock.mockResolvedValue(mockResponse(404));

      const result = await repository.existsById(100);

      expect(result).toBe(false);
    });
  });
});
