import { Module } from '@nestjs/common';
import { UsersService } from './users.service';
import { PrismaService } from '../../../services/prisma.service';
import { UsersController } from './users.controller';

@Module({
  imports: [],
  controllers: [UsersController],
  providers: [UsersService, PrismaService],
  exports: [UsersService],
})
export class UsersModule {}
