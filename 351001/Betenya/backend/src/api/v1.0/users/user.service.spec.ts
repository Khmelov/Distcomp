import { Test, TestingModule } from '@nestjs/testing';
import {
  ForbiddenException,
  NotFoundException,
  InternalServerErrorException,
} from '@nestjs/common';
import { UsersService } from './users.service';
import { PrismaService } from '../../../services/prisma.service';
import { UserRequestTo } from '../../../dto/users/UserRequestTo.dto';
import * as bcrypt from 'bcrypt';
import { UserResponseTo } from '../../../dto/users/UserResponseTo.dto';

jest.mock('bcrypt', () => ({
  genSalt: jest.fn(),
  hash: jest.fn(),
}));

describe('UsersService', () => {
  let service: UsersService;

  const mockUser: UserResponseTo = {
    id: BigInt(1),
    login: 'testuser',
    firstname: 'Test',
    lastname: 'User',
  };

  const mockUserRequest: UserRequestTo = {
    login: 'testuser',
    password: 'password123',
    firstname: 'Test',
    lastname: 'User',
  };

  const mockPrismaService = {
    user: {
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
        UsersService,
        {
          provide: PrismaService,
          useValue: mockPrismaService,
        },
      ],
    }).compile();

    service = module.get<UsersService>(UsersService);

    // Reset all mocks
    jest.clearAllMocks();

    // Setup bcrypt mocks
    (bcrypt.genSalt as jest.Mock).mockResolvedValue('salt');
    (bcrypt.hash as jest.Mock).mockResolvedValue('hashedpassword123');
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('createUser', () => {
    it('should create a new user successfully with hashed password', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(null);
      mockPrismaService.user.create.mockResolvedValue(mockUser);

      const result = await service.createUser(mockUserRequest);

      expect(result).toEqual(mockUser);
      expect(mockPrismaService.user.findUnique).toHaveBeenCalledWith({
        where: { login: mockUserRequest.login },
      });
      expect(bcrypt.genSalt).toHaveBeenCalled();
      expect(bcrypt.hash).toHaveBeenCalledWith('password123', 'salt');
      expect(mockPrismaService.user.create).toHaveBeenCalledWith({
        data: {
          ...mockUserRequest,
          password: 'hashedpassword123',
        },
      });
    });

    it('should throw ForbiddenException if login already exists', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);

      await expect(service.createUser(mockUserRequest)).rejects.toThrow(
        new ForbiddenException('User with this login already exists'),
      );
      expect(mockPrismaService.user.findUnique).toHaveBeenCalledWith({
        where: { login: mockUserRequest.login },
      });
      expect(mockPrismaService.user.create).not.toHaveBeenCalled();
    });

    it('should handle bcrypt errors', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(null);
      (bcrypt.genSalt as jest.Mock).mockRejectedValue(
        new Error('Bcrypt error'),
      );

      await expect(service.createUser(mockUserRequest)).rejects.toThrow(
        'Bcrypt error',
      );
    });

    it('should create user with special characters in login', async () => {
      const specialLoginRequest: UserRequestTo = {
        ...mockUserRequest,
        login: 'user!@#$%',
      };
      const specialLoginUser = { ...mockUser, login: 'user!@#$%' };

      mockPrismaService.user.findUnique.mockResolvedValue(null);
      mockPrismaService.user.create.mockResolvedValue(specialLoginUser);

      const result = await service.createUser(specialLoginRequest);

      expect(result.login).toBe('user!@#$%');
    });
  });

  describe('getAll', () => {
    it('should return all users', async () => {
      const users = [mockUser, { ...mockUser, id: 2, login: 'testuser2' }];
      mockPrismaService.user.findMany.mockResolvedValue(users);

      const result = await service.getAll();

      expect(result).toEqual(users);
      expect(mockPrismaService.user.findMany).toHaveBeenCalledTimes(1);
    });

    it('should return empty array if no users exist', async () => {
      mockPrismaService.user.findMany.mockResolvedValue([]);

      const result = await service.getAll();

      expect(result).toEqual([]);
      expect(mockPrismaService.user.findMany).toHaveBeenCalledTimes(1);
    });
  });

  describe('getUserById', () => {
    it('should return a user by id', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);

      const result = await service.getUserById(1);

      expect(result).toEqual(mockUser);
      expect(mockPrismaService.user.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should throw NotFoundException if user not found', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(null);

      await expect(service.getUserById(999)).rejects.toThrow(
        new NotFoundException('User not found'),
      );
      expect(mockPrismaService.user.findUnique).toHaveBeenCalledWith({
        where: { id: 999 },
      });
    });
  });

  describe('updateUser', () => {
    it('should update a user successfully without rehashing password', async () => {
      const updateData: UserRequestTo = {
        login: 'updateduser',
        password: 'newpassword123',
        firstname: 'Updated',
        lastname: 'Name',
      };
      const updatedUser = { ...mockUser, ...updateData };

      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);
      mockPrismaService.user.update.mockResolvedValue(updatedUser);

      const result = await service.updateUser(1, updateData);

      expect(result).toEqual(updatedUser);
      expect(mockPrismaService.user.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(mockPrismaService.user.update).toHaveBeenCalledWith({
        where: { id: 1 },
        data: updateData,
      });
      // Проверяем, что пароль НЕ хешируется повторно при обновлении
      expect(bcrypt.hash).not.toHaveBeenCalled();
    });

    it('should throw NotFoundException if user not found', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(null);

      await expect(service.updateUser(999, mockUserRequest)).rejects.toThrow(
        new NotFoundException('User not found'),
      );
      expect(mockPrismaService.user.update).not.toHaveBeenCalled();
    });

    it('should throw InternalServerErrorException on database error', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);
      mockPrismaService.user.update.mockRejectedValue(
        new Error('Database error'),
      );

      await expect(service.updateUser(1, mockUserRequest)).rejects.toThrow(
        InternalServerErrorException,
      );
    });

    it('should update user with partial data', async () => {
      const partialUpdate: UserRequestTo = {
        login: 'newlogin',
        password: 'newpass',
        firstname: 'New',
        lastname: 'Name',
      };
      const updatedUser = { ...mockUser, ...partialUpdate };

      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);
      mockPrismaService.user.update.mockResolvedValue(updatedUser);

      const result = await service.updateUser(1, partialUpdate);

      expect(result.login).toBe('newlogin');
      expect(result.firstname).toBe('New');
    });
  });

  describe('deleteUser', () => {
    it('should delete a user successfully', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);
      mockPrismaService.user.delete.mockResolvedValue(mockUser);

      await service.deleteUser(1);

      expect(mockPrismaService.user.findUnique).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(mockPrismaService.user.delete).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should throw NotFoundException if user not found', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(null);

      await expect(service.deleteUser(999)).rejects.toThrow(
        new NotFoundException('User not found'),
      );
      expect(mockPrismaService.user.findUnique).toHaveBeenCalledWith({
        where: { id: 999 },
      });
      expect(mockPrismaService.user.delete).not.toHaveBeenCalled();
    });

    it('should handle database errors on delete', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);
      mockPrismaService.user.delete.mockRejectedValue(
        new Error('Foreign key constraint failed'),
      );

      await expect(service.deleteUser(1)).rejects.toThrow(
        'Foreign key constraint failed',
      );
    });
  });

  describe('password hashing edge cases', () => {
    it('should handle very long passwords', async () => {
      const longPassword = 'a'.repeat(100);
      const requestWithLongPassword: UserRequestTo = {
        ...mockUserRequest,
        password: longPassword,
      };

      mockPrismaService.user.findUnique.mockResolvedValue(null);
      mockPrismaService.user.create.mockResolvedValue({
        ...mockUser,
        password: 'hashedlongpassword',
      });

      (bcrypt.hash as jest.Mock).mockResolvedValue('hashedlongpassword');

      await service.createUser(requestWithLongPassword);

      expect(bcrypt.hash).toHaveBeenCalledWith(longPassword, 'salt');
    });

    it('should handle passwords with special characters', async () => {
      const specialPassword = 'p@ssw0rd!@#$%';
      const requestWithSpecialPassword: UserRequestTo = {
        ...mockUserRequest,
        password: specialPassword,
      };

      mockPrismaService.user.findUnique.mockResolvedValue(null);
      mockPrismaService.user.create.mockResolvedValue({
        ...mockUser,
        password: 'hashedspecial',
      });

      (bcrypt.hash as jest.Mock).mockResolvedValue('hashedspecial');

      await service.createUser(requestWithSpecialPassword);

      expect(bcrypt.hash).toHaveBeenCalledWith(specialPassword, 'salt');
    });
  });

  describe('user with articles relationship', () => {
    it('should handle deletion of user with articles', async () => {
      mockPrismaService.user.findUnique.mockResolvedValue(mockUser);
      mockPrismaService.user.delete.mockRejectedValue(
        new Error('Foreign key constraint failed'),
      );

      await expect(service.deleteUser(1)).rejects.toThrow(
        'Foreign key constraint failed',
      );
    });
  });
});
