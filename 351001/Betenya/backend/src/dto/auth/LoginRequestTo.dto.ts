import { ApiProperty } from '@nestjs/swagger';
import { IsString } from 'class-validator';

export class LoginRequestTo {
  @ApiProperty({ example: 'john@example.com', description: 'User login' })
  @IsString()
  login: string;

  @ApiProperty({ example: 'password123', description: 'User password' })
  @IsString()
  password: string;
}
