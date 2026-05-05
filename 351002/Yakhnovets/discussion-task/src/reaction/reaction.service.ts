import { Injectable, NotFoundException } from '@nestjs/common';
import { CassandraService } from '../cassandra/cassandra.service';
import { ReactionRequestTo } from './dto/reaction-request.to';
import { ReactionResponseTo } from './dto/reaction-response.to';

const TABLE = 'distcomp.tbl_reaction';

@Injectable()
export class ReactionService {
  constructor(private readonly cassandra: CassandraService) {}

  async getAll(): Promise<ReactionResponseTo[]> {
    const result = await this.cassandra.getClient().execute(
      `SELECT id, issue_id, content FROM ${TABLE}`,
    );
    return result.rows.map((row) => CassandraService.rowToReaction(row));
  }

  async getById(id: number): Promise<ReactionResponseTo> {
    const result = await this.cassandra.getClient().execute(
      `SELECT id, issue_id, content FROM ${TABLE} WHERE id = ?`,
      [id],
      { prepare: true },
    );
    const row = result.first();
    if (!row) throw new NotFoundException(`Reaction with id=${id} not found`);
    return CassandraService.rowToReaction(row);
  }

  async create(dto: ReactionRequestTo): Promise<ReactionResponseTo> {
    const id = await this.nextId();
    await this.cassandra.getClient().execute(
      `INSERT INTO ${TABLE} (id, issue_id, content) VALUES (?, ?, ?)`,
      [id, dto.issueId, dto.content],
      { prepare: true },
    );
    return { id, issueId: dto.issueId, content: dto.content };
  }

  async update(id: number, dto: ReactionRequestTo): Promise<ReactionResponseTo> {
    await this.getById(id);
    await this.cassandra.getClient().execute(
      `UPDATE ${TABLE} SET issue_id = ?, content = ? WHERE id = ?`,
      [dto.issueId, dto.content, id],
      { prepare: true },
    );
    return { id, issueId: dto.issueId, content: dto.content };
  }

  async delete(id: number): Promise<void> {
    await this.getById(id);
    await this.cassandra.getClient().execute(
      `DELETE FROM ${TABLE} WHERE id = ?`,
      [id],
      { prepare: true },
    );
  }

  async deleteByIssueId(issueId: number): Promise<void> {
    const result = await this.cassandra.getClient().execute(
      `SELECT id FROM ${TABLE} WHERE issue_id = ? ALLOW FILTERING`,
      [issueId],
      { prepare: true },
    );
    for (const row of result.rows) {
      await this.cassandra.getClient().execute(
        `DELETE FROM ${TABLE} WHERE id = ?`,
        [row.get('id')],
        { prepare: true },
      );
    }
  }

  private async nextId(): Promise<number> {
    const result = await this.cassandra.getClient().execute(`SELECT id FROM ${TABLE}`);
    let max = 0;
    for (const row of result.rows) {
      const v = Number(row.get('id'));
      if (v > max) max = v;
    }
    return max + 1;
  }
}
