import { WriterRole } from './role.enum';

export interface JwtPayload {
  sub: string;
  role: WriterRole;
  iat?: number;
  exp?: number;
}
