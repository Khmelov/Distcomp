import { ApiProperty } from '@nestjs/swagger';

export class StickerResponseTo {
  @ApiProperty({ example: 1, description: 'Sticker ID' })
  id: bigint;

  @ApiProperty({ example: 'Happy', description: 'Name of the Sticker' })
  name: string;
}
