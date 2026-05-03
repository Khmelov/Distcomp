import { Test, TestingModule } from '@nestjs/testing';
import { UsersController } from './users.controller';
import { UsersService } from './users.service';
import { UserRequestTo } from '../../../dto/users/UserRequestTo.dto';
import { UserResponseTo } from '../../../dto/users/UserResponseTo.dto';
import { ForbiddenException, NotFoundException } from '@nestjs/common';

describe('UsersController', () => {
  let controller: UsersController;
  let service: UsersService;

  const mockUserResponse: UserResponseTo = {
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

  const mockUsersService = {
    createUser: jest.fn(),
    getAll: jest.fn(),
    getUserById: jest.fn(),
    updateUser: jest.fn(),
    deleteUser: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UsersController],
      providers: [
        {
          provide: UsersService,
          useValue: mockUsersService,
        },
      ],
    }).compile();

    controller = module.get<UsersController>(UsersController);
    service = module.get<UsersService>(UsersService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('createUser', () => {
    it('should create a new user successfully', async () => {
      mockUsersService.createUser.mockResolvedValue(mockUserResponse);

      const result = await controller.createUser(mockUserRequest);

      expect(result).toEqual(mockUserResponse);
      expect(service.createUser).toHaveBeenCalledWith(mockUserRequest);
      expect(service.createUser).toHaveBeenCalledTimes(1);
    });

    it('should throw ForbiddenException when login already exists', async () => {
      mockUsersService.createUser.mockRejectedValue(
        new ForbiddenException('User with this login already exists'),
      );

      await expect(controller.createUser(mockUserRequest)).rejects.toThrow(
        ForbiddenException,
      );
    });
  });

  describe('getAllUsers', () => {
    it('should return an array of users', async () => {
      const users = [
        mockUserResponse,
        { ...mockUserResponse, id: 2, login: 'testuser2' },
      ];
      mockUsersService.getAll.mockResolvedValue(users);

      const result = await controller.getAllUsers();

      expect(result).toEqual(users);
      expect(service.getAll).toHaveBeenCalledTimes(1);
    });

    it('should return empty array when no users exist', async () => {
      mockUsersService.getAll.mockResolvedValue([]);

      const result = await controller.getAllUsers();

      expect(result).toEqual([]);
      expect(service.getAll).toHaveBeenCalledTimes(1);
    });
  });

  describe('getUserById', () => {
    it('should return a user by id', async () => {
      mockUsersService.getUserById.mockResolvedValue(mockUserResponse);

      const result = await controller.getUserById(1);

      expect(result).toEqual(mockUserResponse);
      expect(service.getUserById).toHaveBeenCalledWith(1);
      expect(service.getUserById).toHaveBeenCalledTimes(1);
    });

    it('should throw NotFoundException when user not found', async () => {
      mockUsersService.getUserById.mockRejectedValue(
        new NotFoundException('User not found'),
      );

      await expect(controller.getUserById(999)).rejects.toThrow(
        NotFoundException,
      );
      expect(service.getUserById).toHaveBeenCalledWith(999);
    });
  });

  describe('updateUser', () => {
    it('should update a user successfully', async () => {
      const updateData: UserRequestTo = {
        login: 'updateduser',
        password: 'newpassword',
        firstname: 'Test',
        lastname: 'User',
      };
      const updatedUser = { ...mockUserResponse, ...updateData };

      mockUsersService.updateUser.mockResolvedValue(updatedUser);

      const result = await controller.updateUser(1, updateData);

      expect(result).toEqual(updatedUser);
      expect(service.updateUser).toHaveBeenCalledWith(1, updateData);
      expect(service.updateUser).toHaveBeenCalledTimes(1);
    });

    it('should throw NotFoundException when user not found', async () => {
      mockUsersService.updateUser.mockRejectedValue(
        new NotFoundException('User not found'),
      );

      await expect(controller.updateUser(999, mockUserRequest)).rejects.toThrow(
        NotFoundException,
      );
    });
  });

  describe('deleteUser', () => {
    it('should delete a user successfully', async () => {
      mockUsersService.deleteUser.mockResolvedValue(undefined);

      const result = await controller.deleteUser(1);

      expect(result).toBeUndefined();
      expect(service.deleteUser).toHaveBeenCalledWith(1);
      expect(service.deleteUser).toHaveBeenCalledTimes(1);
    });

    it('should throw NotFoundException when user not found', async () => {
      mockUsersService.deleteUser.mockRejectedValue(
        new NotFoundException('User not found'),
      );

      await expect(controller.deleteUser(999)).rejects.toThrow(
        NotFoundException,
      );
      expect(service.deleteUser).toHaveBeenCalledWith(999);
    });
  });
});
