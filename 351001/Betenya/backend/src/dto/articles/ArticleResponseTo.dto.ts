import { ApiProperty } from '@nestjs/swagger';
import { Exclude } from 'class-transformer';

export class ArticleResponseTo {
  @ApiProperty({ example: 1, description: 'Article ID' })
  id: bigint;

  @ApiProperty({
    example: 1,
    description: 'User ID',
    default: 0,
    minimum: 0,
  })
  userId: bigint;

  @ApiProperty({
    example: 'Hello, i want to say that...',
    description: 'Content of the Article',
  })
  title: string;

  @ApiProperty({
    example: 'Content of the article...',
    description: 'Content of the Article',
  })
  content: string;

  @ApiProperty({
    description: 'Date of article modification',
  })
  modified: Date;

  @ApiProperty({
    description: 'Date of article created',
  })
  created: Date;
}
