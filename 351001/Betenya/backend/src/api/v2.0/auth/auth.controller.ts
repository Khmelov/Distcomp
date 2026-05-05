import { Body, Controller, HttpCode, Post } from '@nestjs/common';
import { AuthService } from '../../../auth/auth.service';
import { LoginRequestTo } from '../../../dto/auth/LoginRequestTo.dto';

@Controller('v2.0')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('login')
  @HttpCode(200)
  async login(@Body() dto: LoginRequestTo) {
    return this.authService.login(dto);
  }
}
