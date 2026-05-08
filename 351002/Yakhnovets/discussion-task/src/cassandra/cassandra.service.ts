import { Injectable, Logger, OnModuleDestroy, OnModuleInit } from '@nestjs/common';
import { Client, types } from 'cassandra-driver';

const sleep = (ms: number) => new Promise<void>((resolve) => setTimeout(resolve, ms));

@Injectable()
export class CassandraService implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(CassandraService.name);
  private client: Client;

  getClient(): Client {
    return this.client;
  }

  async onModuleInit(): Promise<void> {
    const contactPoints = (process.env.CASSANDRA_CONTACT_POINTS ?? '127.0.0.1').split(',');
    const localDataCenter = process.env.CASSANDRA_LOCAL_DC ?? 'datacenter1';
    const port = Number(process.env.CASSANDRA_PORT ?? 9042);
    const maxAttempts = Number(process.env.CASSANDRA_CONNECT_MAX_ATTEMPTS ?? 60);
    const delayMs = Number(process.env.CASSANDRA_CONNECT_DELAY_MS ?? 2000);

    let lastError: unknown;

    for (let attempt = 1; attempt <= maxAttempts; attempt++) {
      const candidate = new Client({
        contactPoints,
        localDataCenter,
        protocolOptions: { port },
      });

      try {
        await candidate.connect();

        await candidate.execute(`
          CREATE KEYSPACE IF NOT EXISTS distcomp
          WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
        `);

        await candidate.execute(`
          CREATE TABLE IF NOT EXISTS distcomp.tbl_reaction (
            id bigint PRIMARY KEY,
            issue_id bigint,
            content text
          )
        `);

        this.client = candidate;
        if (attempt > 1) {
          this.logger.log(`Cassandra доступна после ${attempt} попыток`);
        }
        return;
      } catch (err) {
        lastError = err;
        await candidate.shutdown().catch(() => undefined);

        if (attempt < maxAttempts) {
          this.logger.warn(
            `Cassandra ${contactPoints.join(',')}:${port} недоступна (${attempt}/${maxAttempts}), повтор через ${delayMs} мс`,
          );
          await sleep(delayMs);
        }
      }
    }

    this.logger.error(
      `Не удалось подключиться к Cassandra за ${maxAttempts} попыток. Запустите БД: в каталоге rest-task выполните docker compose up -d`,
    );
    throw lastError;
  }

  async onModuleDestroy(): Promise<void> {
    if (this.client) {
      await this.client.shutdown();
    }
  }

  static rowToReaction(row: types.Row): { id: number; issueId: number; content: string } {
    return {
      id: Number(row.get('id')),
      issueId: Number(row.get('issue_id')),
      content: String(row.get('content')),
    };
  }
}
