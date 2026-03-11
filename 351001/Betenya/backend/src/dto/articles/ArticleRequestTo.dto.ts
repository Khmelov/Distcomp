import { ApiProperty } from '@nestjs/swagger';
import { IsInt, IsString, MaxLength, Min, MinLength } from 'class-validator';
import { Type } from 'class-transformer';

export class ArticleRequestTo {
  @ApiProperty({
    example: 1,
    description: 'User ID',
    default: 0,
    minimum: 0,
  })
  @IsInt()
  @Min(1, { message: 'User ID must be at least 1' })
  @Type(() => BigInt)
  userId: bigint;

  @ApiProperty({
    example: 'Hello, i want to say that...',
    description: 'Content of the Article',
  })
  @IsString()
  @MinLength(2, {
    message: 'The title must be at least 2 characters long',
  })
  @MaxLength(64, {
    message: 'The title must be no more than 64 characters long',
  })
  title: string;

  @ApiProperty({
    example: 'Content of the article...',
    description: 'Content of the Article',
  })
  @IsString()
  @MinLength(4, {
    message: 'The content must be at least 4 characters long',
  })
  @MaxLength(2048, {
    message: 'The content must be no more than 64 characters long',
  })
  content: string;
}
