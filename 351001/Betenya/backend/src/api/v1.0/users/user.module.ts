import { Module } from '@nestjs/common';
import { UsersService } from './user.service';
import { PrismaService } from '../../../services/prisma.service';
import { UsersController } from './user.controller';

@Module({
  imports: [],
  controllers: [UsersController],
  providers: [UsersService, PrismaService],
  exports: [UsersService],
})
export class UsersModule {}
