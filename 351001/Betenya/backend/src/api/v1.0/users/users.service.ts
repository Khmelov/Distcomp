import {
  ConflictException, ForbiddenException,
  Injectable,
  InternalServerErrorException,
  NotFoundException, UnauthorizedException,
} from '@nestjs/common';
import { PrismaService } from '../../../services/prisma.service';
import { UserResponseTo } from '../../../dto/users/UserResponseTo.dto';
import { UserRequestTo } from '../../../dto/users/UserRequestTo.dto';
import { RedisService } from '../../../redis/redis.service';
import * as bcrypt from 'bcrypt';

const CACHE_PREFIX = 'user';
const CACHE_TTL = 60;

@Injectable()
export class UsersService {
  constructor(
    private prisma: PrismaService,
    private redis: RedisService,
  ) {}

  async createUser(user: UserRequestTo): Promise<UserResponseTo> {
    if (await this.prisma.user.findUnique({ where: { login: user.login } }))
      throw new ForbiddenException('User with this login already exists');

    const salt = await bcrypt.genSalt();
    user.password = await bcrypt.hash(user.password, salt);

    const created = await this.prisma.user.create({
      data: user,
    });

    await this.redis.del(`${CACHE_PREFIX}:all`);

    return created;
  }

  async getAll(): Promise<UserResponseTo[]> {
    const cached = await this.redis.get<UserResponseTo[]>(`${CACHE_PREFIX}:all`);
    if (cached) return cached;

    const users = await this.prisma.user.findMany();
    await this.redis.set(`${CACHE_PREFIX}:all`, users, CACHE_TTL);
    return users;
  }

  async getUserById(id: number): Promise<UserResponseTo> {
    const cached = await this.redis.get<UserResponseTo>(`${CACHE_PREFIX}:${id}`);
    if (cached) return cached;

    const user = await this.prisma.user.findUnique({
      where: { id },
    });

    if (!user) {
      throw new NotFoundException('User not found');
    }

    await this.redis.set(`${CACHE_PREFIX}:${id}`, user, CACHE_TTL);
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
      const updated = await this.prisma.user.update({
        where: { id },
        data: user,
      });

      await this.redis.del(`${CACHE_PREFIX}:${id}`, `${CACHE_PREFIX}:all`);

      return updated;
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

    await this.redis.del(`${CACHE_PREFIX}:${id}`, `${CACHE_PREFIX}:all`);
  }
}
