import { ApiProperty } from '@nestjs/swagger';
import { IsInt, IsString, MaxLength, Min, MinLength } from 'class-validator';
import { Type } from 'class-transformer';

export class NoticeRequestTo {
  @ApiProperty({
    example: 'Hello, i want to say that...',
    description: 'Content of the Notice',
  })
  @IsString()
  @MinLength(2, {
    message: 'The notice content must be at least 2 characters long',
  })
  @MaxLength(2048, {
    message: 'The notice content must be no more than 32 characters long',
  })
  content: string;

  @ApiProperty({
    example: 1,
    description: 'Article ID',
    default: 0,
    minimum: 0,
  })
  @IsInt()
  @Min(1, { message: 'Article ID must be at least 1' })
  @Type(() => BigInt)
  articleId: bigint;
}
