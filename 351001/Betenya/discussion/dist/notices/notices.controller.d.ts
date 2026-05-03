import { NoticesService } from './notices.service';
import { NoticeRequestTo } from '../dto/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../dto/NoticeResponseTo.dto';
export declare class NoticesController {
    private readonly noticesService;
    constructor(noticesService: NoticesService);
    createNotice(dto: NoticeRequestTo): Promise<NoticeResponseTo>;
    getAllNotices(): Promise<NoticeResponseTo[]>;
    getNotice(id: number): Promise<NoticeResponseTo>;
    updateNotice(id: number, dto: NoticeRequestTo): Promise<NoticeResponseTo>;
    deleteNotice(id: number): Promise<void>;
}
