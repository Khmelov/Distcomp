import { IsString, Length } from 'class-validator';

export class LoginDto {
  @IsString()
  @Length(2, 64)
  login!: string;

  @IsString()
  @Length(8, 128)
  password!: string;
}

export class LoginResponseDto {
  access_token!: string;
  token_type!: string;
}