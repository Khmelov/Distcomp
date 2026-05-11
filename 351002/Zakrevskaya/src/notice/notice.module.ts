import { Module } from '@nestjs/common';
import { NoticeController, NoticeV2Controller } from './notice.controller';
import { NoticeService } from './notice.service';
import { NoticeRepository } from './notice.repository';

@Module({
  controllers: [NoticeController, NoticeV2Controller],
  providers: [NoticeService, NoticeRepository],
  exports: [NoticeService, NoticeRepository],
})
export class NoticeModule {}