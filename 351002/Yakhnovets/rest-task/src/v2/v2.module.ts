import { Module } from '@nestjs/common';
import { IssueModule } from '../issue/issue.module';
import { MarkModule } from '../mark/mark.module';
import { ReactionModule } from '../reaction/reaction.module';
import { SecurityModule } from '../security/security.module';
import { WriterModule } from '../writer/writer.module';
import { V2IssueController } from './v2-issue.controller';
import { V2MarkController } from './v2-mark.controller';
import { V2ReactionController } from './v2-reaction.controller';
import { V2WriterController } from './v2-writer.controller';

@Module({
  imports: [WriterModule, IssueModule, MarkModule, ReactionModule, SecurityModule],
  controllers: [V2WriterController, V2IssueController, V2MarkController, V2ReactionController],
})
export class V2Module {}
