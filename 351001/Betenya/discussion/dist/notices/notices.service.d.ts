import { CassandraService } from '../cassandra/cassandra.service';
import { NoticeRequestTo } from '../dto/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../dto/NoticeResponseTo.dto';
export declare class NoticesService {
    private readonly cassandra;
    constructor(cassandra: CassandraService);
    private row;
    private nextId;
    createNotice(dto: NoticeRequestTo): Promise<NoticeResponseTo>;
    getAll(): Promise<NoticeResponseTo[]>;
    getNotice(id: number): Promise<NoticeResponseTo>;
    updateNotice(id: number, dto: NoticeRequestTo): Promise<NoticeResponseTo>;
    deleteNotice(id: number): Promise<void>;
}
