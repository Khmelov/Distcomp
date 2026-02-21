import { ApiProperty } from '@nestjs/swagger';
import { Type } from 'class-transformer';

export class UserResponseTo {
  @ApiProperty({ example: 1, description: 'User ID' })
  @Type(() => BigInt)
  id: bigint;

  @ApiProperty({ example: 'john@example.com', description: 'login' })
  login: string;

  @ApiProperty({ example: 'John', description: 'First name' })
  firstname: string | null;

  @ApiProperty({ example: 'Doe', description: 'Last name' })
  lastname: string | null;
}
