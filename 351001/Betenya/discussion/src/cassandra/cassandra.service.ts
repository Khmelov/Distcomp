import { Injectable, Logger, OnModuleDestroy, OnModuleInit } from '@nestjs/common';
import { Client } from 'cassandra-driver';

const KEYSPACE = 'distcomp';

@Injectable()
export class CassandraService implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(CassandraService.name);

  readonly client: Client;

  constructor() {
    const host = process.env.CASSANDRA_HOST ?? 'localhost';
    const port = parseInt(process.env.CASSANDRA_PORT ?? '9042', 10);

    this.client = new Client({
      contactPoints: [`${host}:${port}`],
      localDataCenter: 'datacenter1',
      // Connect without keyspace first — keyspace may not exist yet
    });
  }

  async onModuleInit(): Promise<void> {
    await this.connectWithRetry();
    await this.initSchema();
  }

  async onModuleDestroy(): Promise<void> {
    await this.client.shutdown();
  }

  private async connectWithRetry(attempts = 15, delayMs = 5000): Promise<void> {
    for (let i = 1; i <= attempts; i++) {
      try {
        await this.client.connect();
        this.logger.log('Connected to Cassandra');
        return;
      } catch (err) {
        this.logger.warn(`Cassandra not ready (attempt ${i}/${attempts}): ${err.message}`);
        if (i === attempts) throw err;
        await new Promise((r) => setTimeout(r, delayMs));
      }
    }
  }

  private async initSchema(): Promise<void> {
    // Create keyspace
    await this.client.execute(
      `CREATE KEYSPACE IF NOT EXISTS ${KEYSPACE}
       WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}`,
    );

    // Notice table — id is the partition key for uniform distribution across nodes.
    // Using articleId alone as partition key would cause data skew
    // (hot partition for popular articles), so id gives even spread.
    await this.client.execute(
      `CREATE TABLE IF NOT EXISTS ${KEYSPACE}.tbl_notice (
         id         bigint PRIMARY KEY,
         article_id bigint,
         content    text
       )`,
    );

    // Counter table for auto-incrementing numeric IDs
    await this.client.execute(
      `CREATE TABLE IF NOT EXISTS ${KEYSPACE}.tbl_counter (
         name  text PRIMARY KEY,
         value counter
       )`,
    );

    this.logger.log('Cassandra schema initialised');
  }
}
