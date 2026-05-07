import { Injectable, Logger, OnModuleInit, OnModuleDestroy } from '@nestjs/common';
import { Kafka, Consumer, Producer, Admin } from 'kafkajs';
import { NoticesService } from '../notices/notices.service';

const STOP_WORDS = [
  'spam',
  'viagra',
  'casino',
  'xxx',
  'hack',
  'malware',
  'phishing',
  'scam',
  'fraud',
];

@Injectable()
export class KafkaService implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(KafkaService.name);
  private readonly kafka: Kafka;
  private readonly producer: Producer;
  private readonly consumer: Consumer;
  private readonly admin: Admin;

  constructor(private readonly noticesService: NoticesService) {
    this.kafka = new Kafka({
      clientId: 'discussion',
      brokers: [process.env.KAFKA_BROKER ?? 'localhost:9092'],
    });
    this.producer = this.kafka.producer();
    this.consumer = this.kafka.consumer({ groupId: 'discussion-group' });
    this.admin = this.kafka.admin();
  }

  async onModuleInit(): Promise<void> {
    try {
      await this.admin.connect();
      await this.ensureTopics();
      await this.admin.disconnect();

      await this.producer.connect();
      await this.consumer.connect();
      await this.consumer.subscribe({ topic: 'InTopic', fromBeginning: true });
      await this.consumer.run({
        eachMessage: async ({ message }) => {
          await this.handleMessage(message);
        },
      });
      this.logger.log('Kafka consumer started on InTopic');
    } catch (err) {
      this.logger.warn(`Kafka init failed (broker may be unavailable): ${err.message}`);
    }
  }

  async onModuleDestroy(): Promise<void> {
    await this.consumer.disconnect().catch(() => {});
    await this.producer.disconnect().catch(() => {});
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

  private moderate(content: string): 'APPROVE' | 'DECLINE' {
    const lower = content.toLowerCase();
    return STOP_WORDS.some((w) => lower.includes(w)) ? 'DECLINE' : 'APPROVE';
  }

  private async handleMessage(message: any): Promise<void> {
    const correlationId = message.headers?.correlationId?.toString();
    let value: any;

    try {
      value = JSON.parse(message.value.toString());
    } catch {
      this.logger.error('Failed to parse Kafka message');
      return;
    }

    this.logger.log(`Received message: method=${value.method}, correlationId=${correlationId}`);

    try {
      let result: any;

      switch (value.method) {
        case 'CREATE': {
          const notice = await this.noticesService.createNotice(value.data, 'PENDING');
          const state = this.moderate(notice.content);
          await this.noticesService.updateNoticeState(notice.id, state);
          result = { ...notice, state };
          break;
        }
        default:
          result = { error: `Unknown method: ${value.method}` };
      }

      await this.producer.send({
        topic: 'OutTopic',
        messages: [
          {
            key: message.key?.toString() ?? null,
            value: JSON.stringify(result),
            headers: { correlationId: correlationId ?? '' },
          },
        ],
      });
    } catch (error) {
      this.logger.error(`Error processing message: ${error.message}`);
      await this.producer.send({
        topic: 'OutTopic',
        messages: [
          {
            key: message.key?.toString() ?? null,
            value: JSON.stringify({ error: error.message }),
            headers: { correlationId: correlationId ?? '' },
          },
        ],
      });
    }
  }
}
