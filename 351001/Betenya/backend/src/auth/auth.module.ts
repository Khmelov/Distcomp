import { Global, Module } from '@nestjs/common';
import { AuthService } from './auth.service';
import { JwtService } from './jwt.service';
import { JwtAuthGuard } from './jwt-auth.guard';
import { RolesGuard } from './roles.guard';
import { PrismaService } from '../services/prisma.service';

@Global()
@Module({
  providers: [AuthService, JwtService, JwtAuthGuard, RolesGuard, PrismaService],
  exports: [AuthService, JwtService, JwtAuthGuard, RolesGuard],
})
export class AuthModule {}
