import { UsersService } from './user.service';
import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  Param,
  ParseIntPipe,
  Post,
  Put,
} from '@nestjs/common';
import { ApiBody, ApiOperation, ApiParam, ApiResponse } from '@nestjs/swagger';
import { UserResponseTo } from '../../../dto/users/UserResponseTo.dto';
import { UserRequestTo } from '../../../dto/users/UserRequestTo.dto';

@Controller()
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Post()
  @ApiOperation({ summary: 'Create new user' })
  @ApiBody({ description: 'New user fields', type: UserRequestTo })
  @ApiResponse({
    status: 201,
    description: 'Created user response',
    type: UserResponseTo,
  })
  @ApiResponse({
    status: 403,
    description: 'User with this login already exists',
  })
  async createUser(@Body() user: UserRequestTo): Promise<UserResponseTo> {
    return this.usersService.createUser(user);
  }

  @Get()
  @ApiOperation({ summary: 'Get users list' })
  @ApiResponse({
    status: 200,
    description: 'Users list',
    type: [UserResponseTo],
  })
  async getAllUsers(): Promise<UserResponseTo[]> {
    return this.usersService.getAll();
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get user by id' })
  @ApiParam({ name: 'id', type: Number, description: 'User ID' })
  @ApiResponse({
    status: 200,
    description: 'Returns user',
    type: UserResponseTo,
  })
  @ApiResponse({ status: 404, description: 'User not found' })
  async getUserById(
    @Param('id', ParseIntPipe) id: number,
  ): Promise<UserResponseTo> {
    return this.usersService.getUserById(id);
  }

  @Put(':id')
  @ApiOperation({ summary: 'Update user' })
  @ApiParam({ name: 'id', type: Number, description: 'User ID' })
  @ApiBody({ description: 'Updated user fields', type: UserRequestTo })
  @ApiResponse({
    status: 200,
    description: 'Returns user',
    type: UserResponseTo,
  })
  @ApiResponse({ status: 404, description: 'User not found' })
  async updateUser(
    @Param('id', ParseIntPipe) id: number,
    @Body() user: UserRequestTo,
  ): Promise<UserResponseTo> {
    return this.usersService.updateUser(id, user);
  }

  @HttpCode(204)
  @Delete(':id')
  @ApiOperation({ summary: 'Delete user' })
  @ApiParam({ name: 'id', type: Number, description: 'User ID' })
  @ApiResponse({ status: 404, description: 'User not found' })
  async deleteUser(@Param('id', ParseIntPipe) id: number): Promise<void> {
    return this.usersService.deleteUser(id);
  }
}
