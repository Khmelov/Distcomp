import { IsInt, IsString, Length, Min } from 'class-validator';

export class ReactionRequestTo {
  @IsInt()
  @Min(1)
  issueId: number;

  @IsString()
  @Length(2, 2048)
  content: string;
}
