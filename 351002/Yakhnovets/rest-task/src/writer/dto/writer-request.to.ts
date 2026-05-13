import { IsString, Length } from 'class-validator';
import { IsEnum, IsOptional } from 'class-validator';
import { WriterRole } from '../../security/role.enum';

export class WriterRequestTo {
  @IsString()
  @Length(2, 64)
  login: string;

  @IsString()
  @Length(8, 128)
  password: string;

  @IsString()
  @Length(2, 64)
  firstname: string;

  @IsString()
  @Length(2, 64)
  lastname: string;

  @IsOptional()
  @IsEnum(WriterRole)
  role?: WriterRole;
}
