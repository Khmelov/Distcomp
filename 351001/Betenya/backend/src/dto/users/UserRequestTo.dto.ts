import { ApiProperty } from '@nestjs/swagger';
import { IsString, MaxLength, MinLength } from 'class-validator';

export class UserRequestTo {
  @ApiProperty({ example: 'john@example.com', description: 'Login' })
  @MinLength(2, { message: 'Login must be at least 2 characters' })
  @MaxLength(64, {
    message: 'Login must be no more than 64 characters long',
  })
  login: string;

  @ApiProperty({ example: 'password123', description: 'User password' })
  @IsString()
  @MinLength(8, { message: 'The password must be at least 8 characters long' })
  @MaxLength(128, {
    message: 'The password must be no more than 128 characters long',
  })
  password: string;

  @ApiProperty({ example: 'John', description: 'First name' })
  @IsString()
  @MinLength(2, {
    message: 'The first name must be at least 2 characters long',
  })
  @MaxLength(64, {
    message: 'The first name must be no more than 64 characters long',
  })
  firstname: string;

  @ApiProperty({ example: 'Doe', description: 'Last name' })
  @IsString()
  @MinLength(2, {
    message: 'The last name must be at least 2 characters long',
  })
  @MaxLength(64, {
    message: 'The last name must be no more than 64 characters long',
  })
  lastname: string;
}
