import { ApiProperty } from '@nestjs/swagger';

export class NoticeResponseTo {
  @ApiProperty({ example: 1, description: 'Notice ID' })
  id: bigint;

  @ApiProperty({
    example: 'Hello, i want to say that...',
    description: 'Content of the Notice',
  })
  content: string;

  @ApiProperty({
    example: 1,
    description: 'Article ID',
    default: 0,
    minimum: 0,
  })
  articleId: bigint;
}
