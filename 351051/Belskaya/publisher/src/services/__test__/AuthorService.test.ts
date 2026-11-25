import { Author } from '../../generated/prisma/client';
import bcrypt from 'bcrypt';
import {
  ConflictError,
  ValidationError,
  NotFoundError,
} from '../../errors/AppError';
import { PrismaAuthorRepository } from '../../repositories/PrismaAuthorRepository';
import { PrismaTopicRepository } from '../../repositories/PrismaTopicRepository';
import { AuthorService } from '../AuthorService';
describe('AuthorService', () => {
  const author: Author = {
    id: 1,
    login: 'john',
    password: 'hashed-password',
    firstname: 'John',
    lastname: 'Smith',
  };

  const validDto = {
    login: 'john',
    password: 'password123',
    firstname: 'John',
    lastname: 'Smith',
  };

  const repository = {
    findByLogin: jest.fn(),
    save: jest.fn(),
    findAll: jest.fn(),
    findById: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  };

  const topicRepository = {
    findById: jest.fn(),
  };

  let service: AuthorService;

  beforeEach(() => {
    jest.clearAllMocks();

    service = new AuthorService(
      repository as unknown as PrismaAuthorRepository,
      topicRepository as unknown as PrismaTopicRepository
    );

    jest.spyOn(bcrypt, 'hashSync').mockReturnValue('hashed-password');
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('create', () => {
    it('should create an author with hashed password', async () => {
      repository.findByLogin.mockResolvedValue(undefined);
      repository.save.mockResolvedValue(author);

      const result = await service.create(validDto);

      expect(repository.findByLogin).toHaveBeenCalledWith('john');
      expect(bcrypt.hashSync).toHaveBeenCalledWith('password123', 10);
      expect(repository.save).toHaveBeenCalledWith({
        ...validDto,
        password: 'hashed-password',
      });
      expect(result).toEqual(author);
    });

    it('should throw ConflictError when login already exists', async () => {
      repository.findByLogin.mockResolvedValue(author);

      await expect(service.create(validDto)).rejects.toThrow(ConflictError);

      expect(repository.save).not.toHaveBeenCalled();
    });

    it('should throw ValidationError when data is invalid', async () => {
      await expect(service.create({ ...validDto, login: 'a' })).rejects.toThrow(
        ValidationError
      );

      expect(repository.findByLogin).not.toHaveBeenCalled();
    });
  });

  describe('findAll', () => {
    it('should return all authors', async () => {
      repository.findAll.mockResolvedValue([author]);

      const result = await service.findAll();

      expect(repository.findAll).toHaveBeenCalledWith(undefined);
      expect(result).toEqual([author]);
    });

    it('should pass pagination parameters to repository', async () => {
      const params = {
        page: 2,
        size: 5,
        sortBy: 'login',
        sortOrder: 'desc' as const,
      };

      repository.findAll.mockResolvedValue([author]);

      await service.findAll(params);

      expect(repository.findAll).toHaveBeenCalledWith(params);
    });
  });

  describe('findById', () => {
    it('should return an author when found', async () => {
      repository.findById.mockResolvedValue(author);

      const result = await service.findById(1);

      expect(repository.findById).toHaveBeenCalledWith(1);
      expect(result).toEqual(author);
    });

    it('should throw NotFoundError when author does not exist', async () => {
      repository.findById.mockResolvedValue(undefined);

      await expect(service.findById(999)).rejects.toThrow(NotFoundError);
    });
  });

  describe('update', () => {
    it('should update an author successfully', async () => {
      const updatedAuthor = { ...author, firstname: 'Jane' };

      repository.findById.mockResolvedValue(author);
      repository.findByLogin.mockResolvedValue(undefined);
      repository.update.mockResolvedValue(updatedAuthor);

      const result = await service.update(1, {
        ...validDto,
        firstname: 'Jane',
      });

      expect(repository.findById).toHaveBeenCalledWith(1);
      expect(repository.findByLogin).toHaveBeenCalledWith('john');
      expect(bcrypt.hashSync).toHaveBeenCalledWith('password123', 10);
      expect(repository.update).toHaveBeenCalledWith(1, {
        ...validDto,
        firstname: 'Jane',
        password: 'hashed-password',
      });
      expect(result).toEqual(updatedAuthor);
    });

    it('should throw NotFoundError when author does not exist', async () => {
      repository.findById.mockResolvedValue(undefined);

      await expect(service.update(999, validDto)).rejects.toThrow(
        NotFoundError
      );

      expect(repository.update).not.toHaveBeenCalled();
    });

    it('should throw ConflictError when login belongs to another author', async () => {
      repository.findById.mockResolvedValue(author);
      repository.findByLogin.mockResolvedValue({
        ...author,
        id: 2,
      });

      await expect(service.update(1, validDto)).rejects.toThrow(ConflictError);

      expect(repository.update).not.toHaveBeenCalled();
    });

    it('should allow keeping the same login', async () => {
      repository.findById.mockResolvedValue(author);
      repository.findByLogin.mockResolvedValue(author);
      repository.update.mockResolvedValue(author);

      await service.update(1, validDto);

      expect(repository.update).toHaveBeenCalled();
    });

    it('should throw ValidationError when data is invalid', async () => {
      await expect(
        service.update(1, { ...validDto, password: '123' })
      ).rejects.toThrow(ValidationError);

      expect(repository.findById).not.toHaveBeenCalled();
    });
  });

  describe('delete', () => {
    it('should delete an author successfully', async () => {
      repository.delete.mockResolvedValue(true);

      await expect(service.delete(1)).resolves.toBeUndefined();

      expect(repository.delete).toHaveBeenCalledWith(1);
    });

    it('should throw NotFoundError when author does not exist', async () => {
      repository.delete.mockResolvedValue(false);

      await expect(service.delete(999)).rejects.toThrow(NotFoundError);
    });
  });

  describe('findByTopicId', () => {
    it('should return the author of a topic', async () => {
      topicRepository.findById.mockResolvedValue({
        id: 10,
        authorId: 1,
      });
      repository.findById.mockResolvedValue(author);

      const result = await service.findByTopicId(10);

      expect(topicRepository.findById).toHaveBeenCalledWith(10);
      expect(repository.findById).toHaveBeenCalledWith(1);
      expect(result).toEqual(author);
    });

    it('should throw NotFoundError when topic does not exist', async () => {
      topicRepository.findById.mockResolvedValue(undefined);

      await expect(service.findByTopicId(999)).rejects.toThrow(NotFoundError);

      await expect(service.findByTopicId(999)).rejects.toMatchObject({
        errorCode: '40402',
      });

      expect(repository.findById).not.toHaveBeenCalled();
    });

    it('should throw NotFoundError when topic author does not exist', async () => {
      topicRepository.findById.mockResolvedValue({
        id: 10,
        authorId: 999,
      });
      repository.findById.mockResolvedValue(undefined);

      await expect(service.findByTopicId(10)).rejects.toThrow(NotFoundError);

      await expect(service.findByTopicId(10)).rejects.toMatchObject({
        errorCode: '40401',
      });
    });
  });
});
