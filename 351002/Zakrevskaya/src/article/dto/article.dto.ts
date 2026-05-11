import { IsString, Length, IsOptional, IsArray, IsInt, IsNotEmpty } from 'class-validator';

export class CreateArticleDto {
  @IsString()
  @Length(2, 64)
  title!: string;

  @IsString()
  @Length(4, 2048)
  content!: string;

  @IsInt()
  @IsNotEmpty()
  userId!: number;

  @IsOptional()
  @IsArray()
  @IsString({ each: true })
  markers?: string[];
}

export class ArticleResponseDto {
  id!: number;
  title!: string;
  content!: string;
  created!: string;
  modified!: string;
  userId!: number;
  markerIds?: number[];
}