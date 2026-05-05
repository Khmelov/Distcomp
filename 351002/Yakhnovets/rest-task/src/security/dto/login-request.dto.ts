import { IsString, Length } from 'class-validator';

export class LoginRequestDto {
  @IsString()
  @Length(2, 64)
  login: string;

  @IsString()
  @Length(8, 128)
  password: string;
}
