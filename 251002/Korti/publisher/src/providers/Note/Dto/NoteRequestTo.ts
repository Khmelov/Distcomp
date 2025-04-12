import {
  IsInt,
  IsOptional,
  IsString,
  MaxLength,
  MinLength,
} from 'class-validator';

export class NoteRequestTo {
  @IsOptional()
  id?: number;

  @IsInt()
  articleId: number;

  @IsString()
  @MinLength(2)
  @MaxLength(2048)
  content: string;
}

export class UpdateNoteTo {
  @IsInt()
  id: number;

  @IsInt()
  articleId: number;

  @IsString()
  @MinLength(2)
  @MaxLength(2048)
  content: string;
}
