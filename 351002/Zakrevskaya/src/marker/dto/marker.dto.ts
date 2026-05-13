import { IsString, Length } from 'class-validator';

export class CreateMarkerDto {
  @IsString()
  @Length(2, 32)
  name!: string;
}

export class MarkerResponseDto {
  id!: number;
  name!: string;
}