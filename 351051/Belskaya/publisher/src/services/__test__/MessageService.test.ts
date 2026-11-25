import { Message } from '../../entities';
import {
  NotFoundError,
  UnprocessableEntityError,
  ValidationError,
} from '../../errors/AppError';
import { DiscussionMessageRepository } from '../../repositories/DiscussionMessageRepository';
import { PrismaTopicRepository } from '../../repositories/PrismaTopicRepository';
import { MessageService } from '../MessageService';

describe('MessageService', () => {
  const message: Message = {
    id: 1,
    topicId: 10,
    content: 'Test message',
  };

  const validDto = {
    topicId: 10,
    content: 'Test message',
  };

  const repository = {
    save: jest.fn(),
    findAll: jest.fn(),
    findById: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
    findByTopicId: jest.fn(),
  };

  const topicRepository = {
    existsById: jest.fn(),
  };

  let service: MessageService;

  beforeEach(() => {
    jest.clearAllMocks();

    service = new MessageService(
      repository as unknown as DiscussionMessageRepository,
      topicRepository as unknown as PrismaTopicRepository
    );
  });

  describe('create', () => {
    it('should create a message successfully', async () => {
      topicRepository.existsById.mockResolvedValue(true);
      repository.save.mockResolvedValue(message);

      const result = await service.create(validDto);

      expect(topicRepository.existsById).toHaveBeenCalledWith(10);
      expect(repository.save).toHaveBeenCalledWith(validDto);
      expect(result).toEqual(message);
    });

    it('should throw UnprocessableEntityError when topic does not exist', async () => {
      topicRepository.existsById.mockResolvedValue(false);

      await expect(service.create(validDto)).rejects.toThrow(
        UnprocessableEntityError
      );

      await expect(service.create(validDto)).rejects.toMatchObject({
        errorCode: '42203',
      });

      expect(repository.save).not.toHaveBeenCalled();
    });

    it('should throw ValidationError when data is invalid', async () => {
      await expect(
        service.create({ ...validDto, content: 'a' })
      ).rejects.toThrow(ValidationError);

      expect(topicRepository.existsById).not.toHaveBeenCalled();
    });
  });

  describe('findAll', () => {
    it('should return all messages', async () => {
      repository.findAll.mockResolvedValue([message]);

      const result = await service.findAll();

      expect(repository.findAll).toHaveBeenCalledWith(undefined);
      expect(result).toEqual([message]);
    });

    it('should pass pagination parameters to repository', async () => {
      const params = {
        page: 2,
        size: 5,
        sortBy: 'content',
        sortOrder: 'desc' as const,
      };

      repository.findAll.mockResolvedValue([message]);

      await service.findAll(params);

      expect(repository.findAll).toHaveBeenCalledWith(params);
    });
  });

  describe('findById', () => {
    it('should return a message when found', async () => {
      repository.findById.mockResolvedValue(message);

      const result = await service.findById(1);

      expect(repository.findById).toHaveBeenCalledWith(1);
      expect(result).toEqual(message);
    });

    it('should throw NotFoundError when message does not exist', async () => {
      repository.findById.mockResolvedValue(undefined);

      await expect(service.findById(999)).rejects.toThrow(NotFoundError);

      await expect(service.findById(999)).rejects.toMatchObject({
        errorCode: '40404',
      });
    });
  });

  describe('update', () => {
    it('should update a message successfully', async () => {
      const updatedMessage = {
        ...message,
        content: 'Updated message',
      };

      repository.findById.mockResolvedValue(message);
      topicRepository.existsById.mockResolvedValue(true);
      repository.update.mockResolvedValue(updatedMessage);

      const result = await service.update(1, {
        ...validDto,
        content: 'Updated message',
      });

      expect(repository.findById).toHaveBeenCalledWith(1);
      expect(topicRepository.existsById).toHaveBeenCalledWith(10);
      expect(repository.update).toHaveBeenCalledWith(1, {
        ...validDto,
        content: 'Updated message',
      });
      expect(result).toEqual(updatedMessage);
    });

    it('should throw NotFoundError when message does not exist', async () => {
      repository.findById.mockResolvedValue(undefined);

      await expect(service.update(999, validDto)).rejects.toThrow(
        NotFoundError
      );

      expect(topicRepository.existsById).not.toHaveBeenCalled();
      expect(repository.update).not.toHaveBeenCalled();
    });

    it('should throw UnprocessableEntityError when topic does not exist', async () => {
      repository.findById.mockResolvedValue(message);
      topicRepository.existsById.mockResolvedValue(false);

      await expect(service.update(1, validDto)).rejects.toThrow(
        UnprocessableEntityError
      );

      await expect(service.update(1, validDto)).rejects.toMatchObject({
        errorCode: '42203',
      });

      expect(repository.update).not.toHaveBeenCalled();
    });

    it('should throw ValidationError when data is invalid', async () => {
      await expect(
        service.update(1, { ...validDto, content: 'a' })
      ).rejects.toThrow(ValidationError);

      expect(repository.findById).not.toHaveBeenCalled();
    });
  });

  describe('delete', () => {
    it('should delete a message successfully', async () => {
      repository.delete.mockResolvedValue(true);

      await expect(service.delete(1)).resolves.toBeUndefined();

      expect(repository.delete).toHaveBeenCalledWith(1);
    });

    it('should throw NotFoundError when message does not exist', async () => {
      repository.delete.mockResolvedValue(false);

      await expect(service.delete(999)).rejects.toThrow(NotFoundError);
    });
  });

  describe('findByTopicId', () => {
    it('should return messages belonging to a topic', async () => {
      topicRepository.existsById.mockResolvedValue(true);
      repository.findByTopicId.mockResolvedValue([message]);

      const result = await service.findByTopicId(10);

      expect(topicRepository.existsById).toHaveBeenCalledWith(10);
      expect(repository.findByTopicId).toHaveBeenCalledWith(10);
      expect(result).toEqual([message]);
    });

    it('should return an empty array when topic has no messages', async () => {
      topicRepository.existsById.mockResolvedValue(true);
      repository.findByTopicId.mockResolvedValue([]);

      const result = await service.findByTopicId(10);

      expect(result).toEqual([]);
    });

    it('should throw NotFoundError when topic does not exist', async () => {
      topicRepository.existsById.mockResolvedValue(false);

      await expect(service.findByTopicId(999)).rejects.toThrow(NotFoundError);

      await expect(service.findByTopicId(999)).rejects.toMatchObject({
        errorCode: '40402',
      });

      expect(repository.findByTopicId).not.toHaveBeenCalled();
    });
  });
});
