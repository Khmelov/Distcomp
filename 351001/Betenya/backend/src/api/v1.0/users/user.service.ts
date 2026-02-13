import {
  ConflictException, ForbiddenException,
  Injectable,
  InternalServerErrorException,
  NotFoundException, UnauthorizedException,
} from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { UserResponseTo } from '../../../dto/users/UserResponseTo.dto';
import { UserRequestTo } from '../../../dto/users/UserRequestTo.dto';
import * as bcrypt from 'bcrypt';

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaService) {}

  async createUser(user: UserRequestTo): Promise<UserResponseTo> {
    if (await this.prisma.user.findUnique({ where: { login: user.login } }))
      throw new ForbiddenException('User with this login already exists');

    const salt = await bcrypt.genSalt();
    user.password = await bcrypt.hash(user.password, salt);

    return this.prisma.user.create({
      data: user,
    });
  }

  async getAll(): Promise<UserResponseTo[]> {
    return this.prisma.user.findMany();
  }

  async getUserById(id: number): Promise<UserResponseTo> {
    const user = await this.prisma.user.findUnique({
      where: { id },
    });

    if (!user) {
      throw new NotFoundException('User not found');
    }

    return user;
  }

  async updateUser(id: number, user: UserRequestTo): Promise<UserResponseTo> {
    const existUser = await this.prisma.user.findUnique({
      where: { id },
    });

    if (!existUser) {
      throw new NotFoundException('User not found');
    }

    try {
      return await this.prisma.user.update({
        where: { id },
        data: user,
      });
    } catch {
      throw new InternalServerErrorException('Database error occurred');
    }
  }

  async deleteUser(id: number): Promise<void> {
    const existUser = await this.prisma.user.findUnique({
      where: { id },
    });

    if (!existUser) {
      throw new NotFoundException('User not found');
    }

    await this.prisma.user.delete({ where: { id } });
  }
}
