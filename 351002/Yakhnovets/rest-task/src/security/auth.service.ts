import { Injectable, UnauthorizedException } from '@nestjs/common';
import * as bcrypt from 'bcryptjs';
import * as jwt from 'jsonwebtoken';
import { WriterService } from '../writer/service/writer.service';
import { LoginResponseDto } from './dto/login-response.dto';
import { JwtPayload } from './jwt-payload.interface';

@Injectable()
export class AuthService {
  private readonly jwtSecret = process.env.JWT_SECRET ?? 'rv-secret-key';
  private readonly expiresInSeconds = Number(process.env.JWT_EXPIRES_IN_SECONDS ?? 60 * 60);

  constructor(private readonly writerService: WriterService) {}

  async login(login: string, password: string): Promise<LoginResponseDto> {
    const writer = await this.writerService.findAuthWriterByLogin(login);
    if (!writer) {
      throw new UnauthorizedException('Invalid login or password');
    }

    const ok = await bcrypt.compare(password, writer.password);
    if (!ok) {
      throw new UnauthorizedException('Invalid login or password');
    }

    const payload: JwtPayload = {
      sub: writer.login,
      role: writer.role,
    };

    return {
      access_token: jwt.sign(payload, this.jwtSecret, {
        expiresIn: this.expiresInSeconds,
      }),
    };
  }

  verifyToken(token: string): JwtPayload {
    try {
      return jwt.verify(token, this.jwtSecret) as JwtPayload;
    } catch {
      throw new UnauthorizedException('Invalid or expired token');
    }
  }
}
