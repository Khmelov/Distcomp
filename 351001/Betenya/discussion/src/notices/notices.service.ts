import {
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { types } from 'cassandra-driver';
import { CassandraService } from '../cassandra/cassandra.service';
import { NoticeRequestTo } from '../dto/NoticeRequestTo.dto';
import { NoticeResponseTo } from '../dto/NoticeResponseTo.dto';

const KS = 'distcomp';

@Injectable()
export class NoticesService {
  constructor(private readonly cassandra: CassandraService) {}

  private row(row: types.Row): NoticeResponseTo {
    return {
      id: (row['id'] as types.Long).toNumber(),
      content: row['content'] as string,
      articleId: (row['article_id'] as types.Long).toNumber(),
      state: (row['state'] as string) ?? 'PENDING',
    };
  }

  private async nextId(): Promise<number> {
    await this.cassandra.client.execute(
      `UPDATE ${KS}.tbl_counter SET value = value + 1 WHERE name = 'notice_id'`,
      [],
      { prepare: true },
    );
    const result = await this.cassandra.client.execute(
      `SELECT value FROM ${KS}.tbl_counter WHERE name = 'notice_id'`,
      [],
      { prepare: true },
    );
    return (result.rows[0]['value'] as types.Long).toNumber();
  }

  async createNotice(dto: NoticeRequestTo, state = 'PENDING'): Promise<NoticeResponseTo> {
    const id = await this.nextId();
    await this.cassandra.client.execute(
      `INSERT INTO ${KS}.tbl_notice (id, article_id, content, state) VALUES (?, ?, ?, ?)`,
      [types.Long.fromNumber(id), types.Long.fromNumber(dto.articleId), dto.content, state],
      { prepare: true },
    );
    return { id, content: dto.content, articleId: dto.articleId, state };
  }

  async getAll(): Promise<NoticeResponseTo[]> {
    const result = await this.cassandra.client.execute(
      `SELECT id, article_id, content, state FROM ${KS}.tbl_notice`,
      [],
      { prepare: true },
    );
    return result.rows.map((r) => this.row(r));
  }

  async getNotice(id: number): Promise<NoticeResponseTo> {
    const result = await this.cassandra.client.execute(
      `SELECT id, article_id, content, state FROM ${KS}.tbl_notice WHERE id = ?`,
      [types.Long.fromNumber(id)],
      { prepare: true },
    );
    if (!result.rows.length) {
      throw new NotFoundException('Notice not found');
    }
    return this.row(result.rows[0]);
  }

  async updateNotice(id: number, dto: NoticeRequestTo): Promise<NoticeResponseTo> {
    const existing = await this.cassandra.client.execute(
      `SELECT id, state FROM ${KS}.tbl_notice WHERE id = ?`,
      [types.Long.fromNumber(id)],
      { prepare: true },
    );
    if (!existing.rows.length) {
      throw new NotFoundException('Notice not found');
    }

    const state = (existing.rows[0]['state'] as string) ?? 'PENDING';

    try {
      await this.cassandra.client.execute(
        `UPDATE ${KS}.tbl_notice SET article_id = ?, content = ? WHERE id = ?`,
        [types.Long.fromNumber(dto.articleId), dto.content, types.Long.fromNumber(id)],
        { prepare: true },
      );
    } catch {
      throw new InternalServerErrorException('Database error occurred');
    }
    return { id, content: dto.content, articleId: dto.articleId, state };
  }

  async updateNoticeState(id: number, state: string): Promise<void> {
    await this.cassandra.client.execute(
      `UPDATE ${KS}.tbl_notice SET state = ? WHERE id = ?`,
      [state, types.Long.fromNumber(id)],
      { prepare: true },
    );
  }

  async deleteNotice(id: number): Promise<void> {
    const existing = await this.cassandra.client.execute(
      `SELECT id FROM ${KS}.tbl_notice WHERE id = ?`,
      [types.Long.fromNumber(id)],
      { prepare: true },
    );
    if (!existing.rows.length) {
      throw new NotFoundException('Notice not found');
    }
    await this.cassandra.client.execute(
      `DELETE FROM ${KS}.tbl_notice WHERE id = ?`,
      [types.Long.fromNumber(id)],
      { prepare: true },
    );
  }
}
