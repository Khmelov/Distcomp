import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { IssueController } from './controller/issue.controller';
import { IssueService } from './service/issue.service';
import { Issue } from './entity/issue.entity';
import { Mark } from '../mark/entity/mark.entity';
import { ReactionModule } from '../reaction/reaction.module';

@Module({
  imports: [TypeOrmModule.forFeature([Issue, Mark]), ReactionModule],
  controllers: [IssueController],
  providers: [IssueService],
  exports: [IssueService],
})
export class IssueModule {}
