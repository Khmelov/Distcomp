import { Injectable, Logger, OnModuleInit, OnModuleDestroy } from '@nestjs/common';
import { Kafka, Consumer, Producer, Admin } from 'kafkajs';
import { randomUUID } from 'crypto';

interface PendingRequest {
  resolve: (value: any) => void;
  reject: (reason: any) => void;
  timer: ReturnType<typeof setTimeout>;
}

@Injectable()
export class KafkaService implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(KafkaService.name);
  private readonly kafka: Kafka;
  private readonly producer: Producer;
  private readonly consumer: Consumer;
  private readonly admin: Admin;
  private readonly pending = new Map<string, PendingRequest>();
  private ready = false;

  constructor() {
    this.kafka = new Kafka({
      clientId: 'publisher',
      brokers: [process.env.KAFKA_BROKER ?? 'localhost:9092'],
    });
    this.producer = this.kafka.producer();
    this.consumer = this.kafka.consumer({ groupId: 'publisher-group' });
    this.admin = this.kafka.admin();
  }

  async onModuleInit(): Promise<void> {
    try {
      await this.admin.connect();
      await this.ensureTopics();
      await this.admin.disconnect();

      await this.producer.connect();
      await this.consumer.connect();
      await this.consumer.subscribe({ topic: 'OutTopic', fromBeginning: false });
      await this.consumer.run({
        eachMessage: async ({ message }) => {
          this.handleReply(message);
        },
      });
      this.ready = true;
      this.logger.log('Kafka producer/consumer ready');
    } catch (err) {
      this.logger.warn(`Kafka init failed (broker may be unavailable): ${err.message}`);
    }
  }

  async onModuleDestroy(): Promise<void> {
    for (const [, req] of this.pending) {
      clearTimeout(req.timer);
      req.reject(new Error('Service shutting down'));
    }
    this.pending.clear();
    await this.consumer.disconnect().catch(() => {});
    await this.producer.disconnect().catch(() => {});
  }

  isReady(): boolean {
    return this.ready;
  }

  private async ensureTopics(): Promise<void> {
    const topics = await this.admin.listTopics();
    const toCreate: { topic: string; numPartitions: number }[] = [];
    if (!topics.includes('InTopic')) {
      toCreate.push({ topic: 'InTopic', numPartitions: 3 });
    }
    if (!topics.includes('OutTopic')) {
      toCreate.push({ topic: 'OutTopic', numPartitions: 3 });
    }
    if (toCreate.length) {
      await this.admin.createTopics({ topics: toCreate });
      this.logger.log(`Created Kafka topics: ${toCreate.map((t) => t.topic).join(', ')}`);
    }
  }

  private handleReply(message: any): void {
    const correlationId = message.headers?.correlationId?.toString();
    if (!correlationId) return;

    const pending = this.pending.get(correlationId);
    if (!pending) return;

    clearTimeout(pending.timer);
    this.pending.delete(correlationId);

    try {
      const data = JSON.parse(message.value.toString());
      if (data.error) {
        pending.reject(new Error(data.error));
      } else {
        pending.resolve(data);
      }
    } catch (err) {
      pending.reject(err);
    }
  }

  async sendAndWait(
    method: string,
    data: Record<string, unknown>,
    key: string,
    timeoutMs = 10000,
  ): Promise<any> {
    const correlationId = randomUUID();

    const promise = new Promise<any>((resolve, reject) => {
      const timer = setTimeout(() => {
        this.pending.delete(correlationId);
        reject(new Error('Kafka request timed out'));
      }, timeoutMs);

      this.pending.set(correlationId, { resolve, reject, timer });
    });

    await this.producer.send({
      topic: 'InTopic',
      messages: [
        {
          key,
          value: JSON.stringify({ method, data }),
          headers: { correlationId },
        },
      ],
    });

    return promise;
  }
}
