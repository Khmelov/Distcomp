import { Injectable, NotFoundException, BadRequestException, HttpException, HttpStatus } from '@nestjs/common';
import { UserRepository } from './user.repository';
import { CreateUserDto, UserResponseDto } from './dto/user.dto';
import { User, UserRole } from './user.entity';
import * as bcrypt from 'bcrypt';

@Injectable()
export class UserService {
  constructor(private readonly userRepository: UserRepository) {}

  async create(createUserDto: CreateUserDto): Promise<UserResponseDto> {
    this.validateUserDto(createUserDto);

    const hashedPassword = await bcrypt.hash(createUserDto.password, 10);

    try {
      const user = await this.userRepository.create({
        login: createUserDto.login,
        password: hashedPassword,
        firstname: createUserDto.firstName,
        lastname: createUserDto.lastName,
        role: createUserDto.role || UserRole.CUSTOMER,
      });
      return this.toResponseDto(user);
    } catch (error: any) {
      if (error.code === '23505' || error.message?.includes('duplicate key')) {
        throw new HttpException('User with this login already exists', HttpStatus.FORBIDDEN);
      }
      throw error;
    }
  }

  async update(id: number, updateUserDto: CreateUserDto): Promise<UserResponseDto> {
    this.validateUserDto(updateUserDto);

    const hashedPassword = await bcrypt.hash(updateUserDto.password, 10);

    try {
      const user = await this.userRepository.update(id, {
        login: updateUserDto.login,
        password: hashedPassword,
        firstname: updateUserDto.firstName,
        lastname: updateUserDto.lastName,
        role: updateUserDto.role || UserRole.CUSTOMER,
      });
      return this.toResponseDto(user);
    } catch (error: any) {
      if (error.code === '23505' || error.message?.includes('duplicate key')) {
        throw new HttpException('User with this login already exists', HttpStatus.FORBIDDEN);
      }
      throw error;
    }
  }

  async delete(id: number): Promise<void> {
    const user = await this.userRepository.findById(id);
    if (!user) throw new NotFoundException('User not found');
    await this.userRepository.delete(id);
  }

  async findById(id: number): Promise<UserResponseDto> {
    const user = await this.userRepository.findById(id);
    if (!user) throw new NotFoundException('User not found');
    return this.toResponseDto(user);
  }

  async findAll(page: number = 1, limit: number = 10): Promise<UserResponseDto[]> {
    const [users] = await this.userRepository.findAll({ page, limit });
    return users.map(user => this.toResponseDto(user));
  }

  private validateUserDto(dto: CreateUserDto) {
    if (dto.login.length < 2 || dto.login.length > 64) {
      throw new BadRequestException('Login must be 2-64 characters');
    }
    if (dto.password.length < 8 || dto.password.length > 128) {
      throw new BadRequestException('Password must be 8-128 characters');
    }
    if (dto.firstName.length < 2 || dto.firstName.length > 64) {
      throw new BadRequestException('firstName must be 2-64 characters');
    }
    if (dto.lastName.length < 2 || dto.lastName.length > 64) {
      throw new BadRequestException('lastName must be 2-64 characters');
    }
  }

  private toResponseDto(user: User): UserResponseDto {
    return {
      id: user.id,
      login: user.login,
      firstName: user.firstname,
      lastName: user.lastname,
      role: user.role,
    };
  }
}