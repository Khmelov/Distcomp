import { Body, Controller, HttpCode, Post, UseFilters } from '@nestjs/common';
import { AuthService } from './auth.service';
import { LoginRequestDto } from './dto/login-request.dto';
import { LoginResponseDto } from './dto/login-response.dto';
import { Public } from './public.decorator';
import { V2HttpExceptionFilter } from '../v2/v2-http-exception.filter';

@UseFilters(V2HttpExceptionFilter)
@Controller('api/v2.0/login')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Public()
  @Post()
  @HttpCode(200)
  login(@Body() dto: LoginRequestDto): Promise<LoginResponseDto> {
    return this.authService.login(dto.login, dto.password);
  }
}
