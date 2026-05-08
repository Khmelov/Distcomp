import { Module } from '@nestjs/common';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { WriterModule } from '../writer/writer.module';

@Module({
  imports: [WriterModule],
  controllers: [AuthController],
  providers: [AuthService],
  exports: [AuthService],
})
export class SecurityModule {}
