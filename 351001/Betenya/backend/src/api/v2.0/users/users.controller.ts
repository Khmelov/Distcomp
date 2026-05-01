import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpException,
  HttpStatus,
  Param,
  ParseIntPipe,
  Post,
  Put,
  Req,
  UseGuards,
} from '@nestjs/common';
import { UsersService } from '../../v1.0/users/users.service';
import { AuthService } from '../../../auth/auth.service';
import { JwtAuthGuard } from '../../../auth/jwt-auth.guard';
import { RegisterRequestTo } from '../../../dto/auth/RegisterRequestTo.dto';
import { UserRequestTo } from '../../../dto/users/UserRequestTo.dto';

@Controller('v2.0/users')
export class V2UsersController {
  constructor(
    private readonly usersService: UsersService,
    private readonly authService: AuthService,
  ) {}

  @Post()
  async register(@Body() dto: RegisterRequestTo) {
    return this.authService.register(dto);
  }

  @Get()
  @UseGuards(JwtAuthGuard)
  async getAll() {
    return this.usersService.getAll();
  }

  @Get(':id')
  @UseGuards(JwtAuthGuard)
  async getById(@Param('id', ParseIntPipe) id: number) {
    return this.usersService.getUserById(id);
  }

  @Put(':id')
  @UseGuards(JwtAuthGuard)
  async update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: UserRequestTo,
    @Req() req: any,
  ) {
    if (req.user.role !== 'ADMIN' && req.user.id !== id) {
      throw new HttpException(
        { errorMessage: 'Access denied', errorCode: 40300 },
        HttpStatus.FORBIDDEN,
      );
    }
    return this.usersService.updateUser(id, dto);
  }

  @HttpCode(204)
  @Delete(':id')
  @UseGuards(JwtAuthGuard)
  async delete(@Param('id', ParseIntPipe) id: number, @Req() req: any) {
    if (req.user.role !== 'ADMIN' && req.user.id !== id) {
      throw new HttpException(
        { errorMessage: 'Access denied', errorCode: 40300 },
        HttpStatus.FORBIDDEN,
      );
    }
    return this.usersService.deleteUser(id);
  }
}
