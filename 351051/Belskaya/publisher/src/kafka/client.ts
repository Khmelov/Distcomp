import { Kafka } from 'kafkajs';

export const kafka = new Kafka({
  clientId: 'publisher-service',
  brokers: ['localhost:9092'],
});
