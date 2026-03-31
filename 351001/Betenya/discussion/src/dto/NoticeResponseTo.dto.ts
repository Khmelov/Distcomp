import { ApiProperty } from '@nestjs/swagger';

export class NoticeResponseTo {
  @ApiProperty({ example: 1, description: 'Notice ID' })
  id: number;

  @ApiProperty({ example: 'Hello, i want to say that...', description: 'Content of the Notice' })
  content: string;

  @ApiProperty({ example: 1, description: 'Article ID' })
  articleId: number;

  @ApiProperty({ example: 'PENDING', description: 'Moderation state', enum: ['PENDING', 'APPROVE', 'DECLINE'] })
  state: string;
}
