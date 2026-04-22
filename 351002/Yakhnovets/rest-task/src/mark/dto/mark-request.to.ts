import { IsString, Length } from 'class-validator';

export class MarkRequestTo {
  @IsString()
  @Length(2, 32)
  name: string;
}
