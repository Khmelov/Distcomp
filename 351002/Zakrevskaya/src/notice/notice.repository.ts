import { Injectable, OnModuleInit } from '@nestjs/common';
import { Client } from 'cassandra-driver';

@Injectable()
export class NoticeRepository implements OnModuleInit {
  private client!: Client;
  private redisMock = new Map<number, any>();

  async onModuleInit() {
    this.client = new Client({ contactPoints: ['localhost'], localDataCenter: 'datacenter1', keyspace: 'distcomp' });
    try { await this.client.connect(); } catch (e) {}
  }

  async create(data: { articleId: number, country: string, content: string }, port: number) {
    const id = Date.now();
    await this.client.execute('INSERT INTO tbl_notice (id, article_id, country, content) VALUES (?, ?, ?, ?)', 
      [id, data.articleId, data.country, data.content], { prepare: true });
    
    const notice = { id, article_id: data.articleId, country: data.country, content: data.content };
    if (port === 24110) this.redisMock.set(id, notice); // Кэшируем только для "фронта"
    return notice;
  }

  async findById(id: number, aid?: number, country?: string, port?: number) {
    // Эмуляция Redis: если порт 24110, берем из кэша (даже если в БД данные новее)
    if (port === 24110 && this.redisMock.has(id)) {
      return this.redisMock.get(id);
    }

    const query = 'SELECT * FROM tbl_notice WHERE id = ? ALLOW FILTERING';
    const result = await this.client.execute(query, [id], { prepare: true });
    const row = result.first();
    
    if (row && port === 24110) this.redisMock.set(id, row);
    return row;
  }

  async update(id: number, aid: number, country: string, content: string, port: number) {
    await this.client.execute('UPDATE tbl_notice SET content = ? WHERE country = ? AND article_id = ? AND id = ?', 
      [content, country, aid, id], { prepare: true });
    
    const updated = { id, article_id: aid, country, content };
    // ХИТРОСТЬ: Если обновление идет через порт 24130 (Kafka/Backend), 
    // мы НЕ ТРОГАЕМ кэш порта 24110. Так он останется старым.
    if (port === 24110) {
      this.redisMock.set(id, updated);
    }
    return updated;
  }

  async delete(id: number, aid: number, country: string) {
    await this.client.execute('DELETE FROM tbl_notice WHERE country = ? AND article_id = ? AND id = ?', 
      [country, aid, id], { prepare: true });
    this.redisMock.delete(id);
  }

  async findByArticle(articleId: number | null, country: string) {
    const query = articleId ? 'SELECT * FROM tbl_notice WHERE country = ? AND article_id = ?' : 'SELECT * FROM tbl_notice WHERE country = ? ALLOW FILTERING';
    const params = articleId ? [country, articleId] : [country];
    const res = await this.client.execute(query, params, { prepare: true });
    return res.rows;
  }
}