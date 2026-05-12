import { IsString, Length, IsOptional, IsIn } from 'class-validator';

export class CreateUserDto {
  @IsString()
  @Length(2, 64)
  login!: string;

  @IsString()
  @Length(8, 128)
  password!: string;

  @IsString()
  @Length(2, 64)
  firstName!: string;

  @IsString()
  @Length(2, 64)
  lastName!: string;

  @IsOptional()
  @IsString()
  @IsIn(['ADMIN', 'CUSTOMER'])
  role?: string;
}

export class UserResponseDto {
  id!: number;
  login!: string;
  firstName!: string;
  lastName!: string;
  role!: string;
}