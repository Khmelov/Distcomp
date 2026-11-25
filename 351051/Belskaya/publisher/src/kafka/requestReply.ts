import { randomUUID } from 'crypto';
import { kafka } from './client';

const producer = kafka.producer();
const consumer = kafka.consumer({ groupId: 'publisher-group' });

interface PendingRequest {
  resolve: (value: any) => void;
  reject: (reason: any) => void;
  timer: NodeJS.Timeout;
}

const pending = new Map<string, PendingRequest>();

let started = false;

export async function startKafkaRequestReply() {
  if (started) return;
  started = true;

  await producer.connect();
  await consumer.connect();
  await consumer.subscribe({ topic: 'OutTopic', fromBeginning: true });

  await consumer.run({
    eachMessage: async ({ message }) => {
      const raw = message.value?.toString();
      if (!raw) return;

      let payload: any;
      try {
        payload = JSON.parse(raw);
      } catch {
        console.error('[publisher] Invalid JSON in OutTopic:', raw);
        return;
      }

      const correlationId = payload.correlationId;
      if (!correlationId) return;

      const req = pending.get(correlationId);
      if (!req) return;

      clearTimeout(req.timer);
      pending.delete(correlationId);
      req.resolve(payload);
    },
  });

  console.log('[publisher] Kafka request-reply ready');
}

export async function sendKafkaRequest(
  action: string,
  data: any,
  timeoutMs = 1000
): Promise<any> {
  const correlationId = randomUUID();

  const promise = new Promise<any>((resolve, reject) => {
    const timer = setTimeout(() => {
      pending.delete(correlationId);
      reject(new Error(`Kafka timeout for action ${action}`));
    }, timeoutMs);

    pending.set(correlationId, { resolve, reject, timer });
  });

  await producer.send({
    topic: 'InTopic',
    messages: [
      {
        key: String(data?.topicId ?? data?.id ?? correlationId),
        value: JSON.stringify({ action, data, correlationId }),
      },
    ],
  });

  return promise;
}
