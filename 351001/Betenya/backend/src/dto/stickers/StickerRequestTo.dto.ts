import { ApiProperty } from '@nestjs/swagger';
import { IsString, MaxLength, MinLength } from 'class-validator';

export class StickerRequestTo {
  @ApiProperty({ example: 'Happy', description: 'Name of the Sticker' })
  @IsString()
  @MinLength(2, {
    message: 'The sticker name must be at least 2 characters long',
  })
  @MaxLength(32, {
    message: 'The sticker name must be no more than 32 characters long',
  })
  name: string;
}
