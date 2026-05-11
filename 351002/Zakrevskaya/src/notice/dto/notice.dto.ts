import { IsString, Length, IsInt, IsNotEmpty } from 'class-validator';

export class CreateNoticeDto {
  @IsString()
  @Length(2, 2048)
  content!: string;

  @IsInt()
  @IsNotEmpty()
  articleId!: number;
}

export class NoticeResponseDto {
  id!: number;
  content!: string;
  articleId!: number;
}