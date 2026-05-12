import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { PrismaService } from '../services/prisma.service';
import { JwtService } from './jwt.service';
import { RegisterRequestTo } from '../dto/auth/RegisterRequestTo.dto';
import { LoginRequestTo } from '../dto/auth/LoginRequestTo.dto';
import * as bcrypt from 'bcrypt';

@Injectable()
export class AuthService {
  constructor(
    private prisma: PrismaService,
    private jwtService: JwtService,
  ) {}

  async register(dto: RegisterRequestTo) {
    const existing = await this.prisma.user.findUnique({
      where: { login: dto.login },
    });

    if (existing) {
      throw new HttpException(
        { errorMessage: 'User with this login already exists', errorCode: 40300 },
        HttpStatus.FORBIDDEN,
      );
    }

    const salt = await bcrypt.genSalt();
    const hashedPassword = await bcrypt.hash(dto.password, salt);

    const user = await this.prisma.user.create({
      data: {
        login: dto.login,
        password: hashedPassword,
        firstname: dto.firstname,
        lastname: dto.lastname,
        role: dto.role || 'CUSTOMER',
      },
    });

    return {
      id: user.id,
      login: user.login,
      firstname: user.firstname,
      lastname: user.lastname,
      role: user.role,
    };
  }

  async login(dto: LoginRequestTo) {
    const user = await this.prisma.user.findUnique({
      where: { login: dto.login },
    });

    if (!user) {
      throw new HttpException(
        { errorMessage: 'Invalid credentials', errorCode: 40100 },
        HttpStatus.UNAUTHORIZED,
      );
    }

    const passwordMatch = await bcrypt.compare(dto.password, user.password);
    if (!passwordMatch) {
      throw new HttpException(
        { errorMessage: 'Invalid credentials', errorCode: 40100 },
        HttpStatus.UNAUTHORIZED,
      );
    }

    const token = this.jwtService.sign({
      sub: user.login,
      id: Number(user.id),
      role: user.role,
    });

    return { access_token: token };
  }
}
