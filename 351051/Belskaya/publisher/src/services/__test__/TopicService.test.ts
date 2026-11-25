import { Topic } from '@prisma/client';
import { ConflictError, ValidationError } from '../../errors/AppError';
import { PrismaAuthorRepository } from '../../repositories/PrismaAuthorRepository';
import { PrismaTopicRepository } from '../../repositories/PrismaTopicRepository';
import { TopicService } from '../TopicService';

describe('TopicService', () => {
  const topic: Topic = {
    id: 1,
    authorId: 10,
    title: 'Test topic',
    content: 'Test content',
    created: new Date('2026-01-01T10:00:00.000Z'),
    modified: new Date('2026-01-01T10:00:00.000Z'),
  };

  const validDto = {
    authorId: 10,
    title: 'Test topic',
    content: 'Test content',
  };

  const repository = {
    saveWithMarkers: jest.fn(),
    findAll: jest.fn(),
    findById: jest.fn(),
    existsByTitle: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  };

  const authorRepository = {
    existsById: jest.fn(),
    findByLogin: jest.fn(),
  };

  let service: TopicService;

  beforeEach(() => {
    jest.clearAllMocks();

    service = new TopicService(
      repository as unknown as PrismaTopicRepository,
      authorRepository as unknown as PrismaAuthorRepository
    );
  });

  describe('create', () => {
    it('should create a topic successfully', async () => {
      authorRepository.existsById.mockResolvedValue(true);
      repository.existsByTitle.mockResolvedValue(false);
      repository.saveWithMarkers.mockResolvedValue(topic);

      const result = await service.create(validDto);

      expect(authorRepository.existsById).toHaveBeenCalledWith(10);
      expect(repository.existsByTitle).toHaveBeenCalledWith('Test topic');
      expect(repository.saveWithMarkers).toHaveBeenCalledWith(
        expect.objectContaining({
          ...validDto,
          created: expect.any(Date),
          modified: expect.any(Date),
        }),
        []
      );
      expect(result).toEqual(topic);
    });

    it('should create a topic with markers', async () => {
      authorRepository.existsById.mockResolvedValue(true);
      repository.existsByTitle.mockResolvedValue(false);
      repository.saveWithMarkers.mockResolvedValue(topic);

      const dto = {
        ...validDto,
        markers: ['red', 'green', 'blue'],
      };

      await service.create(dto);

      expect(repository.saveWithMarkers).toHaveBeenCalledWith(
        expect.any(Object),
        ['red', 'green', 'blue']
      );
    });

    it('should throw UnprocessableEntityError when author does not exist', async () => {
      authorRepository.existsById.mockResolvedValue(false);

      await expect(service.create(validDto)).rejects.toMatchObject({
        errorCode: '42202',
      });

      expect(repository.existsByTitle).not.toHaveBeenCalled();
      expect(repository.saveWithMarkers).not.toHaveBeenCalled();
    });

    it('should throw ConflictError when topic title already exists', async () => {
      authorRepository.existsById.mockResolvedValue(true);
      repository.existsByTitle.mockResolvedValue(true);

      await expect(service.create(validDto)).rejects.toThrow(ConflictError);

      expect(repository.saveWithMarkers).not.toHaveBeenCalled();
    });

    it('should throw ValidationError when data is invalid', async () => {
      await expect(service.create({ ...validDto, title: 'a' })).rejects.toThrow(
        ValidationError
      );

      expect(authorRepository.existsById).not.toHaveBeenCalled();
    });
  });

  describe('findAll', () => {
    it('should return all topics', async () => {
      repository.findAll.mockResolvedValue([topic]);

      const result = await service.findAll();

      expect(repository.findAll).toHaveBeenCalledWith(undefined);
      expect(result).toEqual([topic]);
    });

    it('should pass pagination parameters to repository', async () => {
      const params = {
        page: 2,
        size: 5,
        sortBy: 'title',
        sortOrder: 'desc' as const,
      };

      repository.findAll.mockResolvedValue([topic]);

      await service.findAll(params);

      expect(repository.findAll).toHaveBeenCalledWith(params);
    });
  });

  describe('findById', () => {
    it('should return a topic when found', async () => {
      repository.findById.mockResolvedValue(topic);

      const result = await service.findById(1);

      expect(repository.findById).toHaveBeenCalledWith(1);
      expect(result).toEqual(topic);
    });

    it('should throw NotFoundError when topic does not exist', async () => {
      repository.findById.mockResolvedValue(undefined);

      await expect(service.findById(999)).rejects.toMatchObject({
        errorCode: '40402',
      });
    });
  });

  describe('update', () => {
    it('should update a topic successfully', async () => {
      const updatedTopic = {
        ...topic,
        title: 'Updated topic',
      };

      repository.findById.mockResolvedValue(topic);
      authorRepository.existsById.mockResolvedValue(true);
      repository.update.mockResolvedValue(updatedTopic);

      const result = await service.update(1, {
        ...validDto,
        title: 'Updated topic',
      });

      expect(repository.findById).toHaveBeenCalledWith(1);
      expect(authorRepository.existsById).toHaveBeenCalledWith(10);
      expect(repository.update).toHaveBeenCalledWith(
        1,
        expect.objectContaining({
          ...validDto,
          title: 'Updated topic',
          modified: expect.any(Date),
        })
      );
      expect(result).toEqual(updatedTopic);
    });

    it('should throw NotFoundError when topic does not exist', async () => {
      repository.findById.mockResolvedValue(undefined);

      await expect(service.update(999, validDto)).rejects.toMatchObject({
        errorCode: '40402',
      });

      expect(authorRepository.existsById).not.toHaveBeenCalled();
      expect(repository.update).not.toHaveBeenCalled();
    });

    it('should throw UnprocessableEntityError when author does not exist', async () => {
      repository.findById.mockResolvedValue(topic);
      authorRepository.existsById.mockResolvedValue(false);

      await expect(service.update(1, validDto)).rejects.toMatchObject({
        errorCode: '42202',
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
    it('should delete a topic successfully', async () => {
      repository.delete.mockResolvedValue(true);

      await expect(service.delete(1)).resolves.toBeUndefined();

      expect(repository.delete).toHaveBeenCalledWith(1);
    });

    it('should throw NotFoundError when topic does not exist', async () => {
      repository.delete.mockResolvedValue(false);

      await expect(service.delete(999)).rejects.toMatchObject({
        errorCode: '40402',
      });
    });
  });

  describe('findByFilters', () => {
    const secondTopic: Topic = {
      ...topic,
      id: 2,
      authorId: 20,
      title: 'Another topic',
      content: 'Different content',
    };

    beforeEach(() => {
      repository.findAll.mockResolvedValue([topic, secondTopic]);
    });

    it('should return all topics when filters are empty', async () => {
      const result = await service.findByFilters({});

      expect(repository.findAll).toHaveBeenCalled();
      expect(result).toEqual([topic, secondTopic]);
    });

    it('should filter topics by author login', async () => {
      authorRepository.findByLogin.mockResolvedValue({
        id: 10,
        login: 'john',
      });

      const result = await service.findByFilters({
        authorLogin: 'john',
      });

      expect(authorRepository.findByLogin).toHaveBeenCalledWith('john');
      expect(result).toEqual([topic]);
    });

    it('should return an empty array when author does not exist', async () => {
      authorRepository.findByLogin.mockResolvedValue(undefined);

      const result = await service.findByFilters({
        authorLogin: 'unknown',
      });

      expect(result).toEqual([]);
    });

    it('should filter topics by title', async () => {
      const result = await service.findByFilters({
        title: 'Another',
      });

      expect(result).toEqual([secondTopic]);
    });

    it('should filter topics by content', async () => {
      const result = await service.findByFilters({
        content: 'Different',
      });

      expect(result).toEqual([secondTopic]);
    });

    it('should apply multiple filters together', async () => {
      authorRepository.findByLogin.mockResolvedValue({
        id: 10,
        login: 'john',
      });

      const result = await service.findByFilters({
        authorLogin: 'john',
        title: 'Test',
        content: 'Test',
      });

      expect(result).toEqual([topic]);
    });
  });
});
