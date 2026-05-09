import { Injectable, Logger, OnModuleDestroy, OnModuleInit } from '@nestjs/common';
import { createClient } from 'redis';
import { ReactionResponseTo } from '../dto/reaction-response.to';

@Injectable()
export class ReactionCacheService implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(ReactionCacheService.name);
  private client?: ReturnType<typeof createClient>;

  async onModuleInit(): Promise<void> {
    const client = createClient({
      url: process.env.REDIS_URL ?? 'redis://127.0.0.1:6379',
    });

    client.on('error', (error) => {
      if (client.isOpen) {
        this.logger.warn(`Redis error: ${this.toMessage(error)}`);
      }
    });

    try {
      await client.connect();
      this.client = client;
    } catch (error) {
      this.logger.warn(`Redis unavailable, reaction cache disabled: ${this.toMessage(error)}`);
      if (client.isOpen) {
        await client.disconnect();
      }
    }
  }

  async onModuleDestroy(): Promise<void> {
    if (this.client?.isOpen) {
      await this.client.quit().catch(() => undefined);
    }
  }

  async get(id: number): Promise<ReactionResponseTo | null> {
    if (!this.client?.isOpen) {
      return null;
    }

    try {
      const value = await this.client.get(this.key(id));
      return value ? (JSON.parse(value) as ReactionResponseTo) : null;
    } catch (error) {
      this.logger.warn(`Redis read failed: ${this.toMessage(error)}`);
      return null;
    }
  }

  async set(reaction: ReactionResponseTo): Promise<void> {
    if (!this.client?.isOpen) {
      return;
    }

    try {
      await this.client.set(this.key(reaction.id), JSON.stringify(reaction));
    } catch (error) {
      this.logger.warn(`Redis write failed: ${this.toMessage(error)}`);
    }
  }

  async delete(id: number): Promise<void> {
    if (!this.client?.isOpen) {
      return;
    }

    try {
      await this.client.del(this.key(id));
    } catch (error) {
      this.logger.warn(`Redis delete failed: ${this.toMessage(error)}`);
    }
  }

  private key(id: number): string {
    return `tbl_reaction:${id}`;
  }

  private toMessage(error: unknown): string {
    return error instanceof Error ? error.message : String(error);
  }
}
