import { IsArray, IsInt, IsOptional, IsString, Length, Min } from 'class-validator';

export class IssueRequestTo {
  @IsInt()
  @Min(1)
  writerId: number;

  @IsString()
  @Length(2, 64)
  title: string;

  @IsString()
  @Length(4, 2048)
  content: string;

  @IsOptional()
  @IsArray()
  @IsString({ each: true })
  @Length(2, 32, { each: true })
  marks?: string[];
}
