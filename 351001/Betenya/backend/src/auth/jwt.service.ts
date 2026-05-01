import { Injectable } from '@nestjs/common';
import * as jwt from 'jsonwebtoken';

const SECRET = process.env.JWT_SECRET || 'distcomp-jwt-secret-key-2024';
const EXPIRES_IN = '1h';

export interface JwtPayload {
  sub: string;
  id: number;
  role: string;
  iat?: number;
  exp?: number;
}

@Injectable()
export class JwtService {
  sign(payload: { sub: string; id: number; role: string }): string {
    return jwt.sign(payload, SECRET, { expiresIn: EXPIRES_IN });
  }

  verify(token: string): JwtPayload {
    return jwt.verify(token, SECRET) as JwtPayload;
  }
}
