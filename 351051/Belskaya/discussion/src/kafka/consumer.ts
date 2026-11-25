import {
  MessageRepository,
  MessageState,
} from "../repositories/MessageRepository";
import { kafka } from "./client";

const consumer = kafka.consumer({ groupId: "discussion-group" });
const producer = kafka.producer();
const repository = new MessageRepository();

const STOP_WORDS = ["badword", "spam", "bad"];

export async function startKafkaConsumer() {
  await consumer.connect();
  await producer.connect();
  await consumer.subscribe({ topic: "InTopic", fromBeginning: true });

  await consumer.run({
    eachMessage: async ({ message }) => {
      const raw = message.value?.toString();
      if (!raw) return;

      let payload: any;
      try {
        payload = JSON.parse(raw);
      } catch (err) {
        console.error("Invalid JSON in InTopic:", raw);
        return;
      }

      const { action, data, correlationId } = payload;
      console.log(`[discussion] Received action=${action}`, data);

      try {
        switch (action) {
          case "CREATE": {
            const { id, topicId, country, content } = data;
            const hasStopWord = STOP_WORDS.some((w) =>
              content.toLowerCase().includes(w),
            );
            const state: MessageState = hasStopWord ? "DECLINE" : "APPROVE";

            const newMessage = {
              id: Number(id),
              topicId: Number(topicId),
              country,
              content,
              state,
            };

            await repository.save(newMessage);

            await producer.send({
              topic: "OutTopic",
              messages: [
                {
                  key: String(topicId),
                  value: JSON.stringify({
                    action: "CREATE_RESULT",
                    correlationId,
                    data: newMessage,
                  }),
                },
              ],
            });
            break;
          }

          case "GET_ALL": {
            const messages = await repository.findAll();
            await producer.send({
              topic: "OutTopic",
              messages: [
                {
                  key: "all",
                  value: JSON.stringify({
                    action: "GET_ALL_RESULT",
                    correlationId,
                    data: messages,
                  }),
                },
              ],
            });
            break;
          }

          case "GET_BY_ID": {
            const message = await repository.findById(Number(data.id));
            await producer.send({
              topic: "OutTopic",
              messages: [
                {
                  key: String(data.id),
                  value: JSON.stringify({
                    action: "GET_BY_ID_RESULT",
                    correlationId,
                    data: message ?? null,
                  }),
                },
              ],
            });
            break;
          }

          case "GET_BY_TOPIC": {
            const messages = await repository.findByTopicId(
              Number(data.topicId),
              data.sortOrder ?? "asc",
            );
            await producer.send({
              topic: "OutTopic",
              messages: [
                {
                  key: String(data.topicId),
                  value: JSON.stringify({
                    action: "GET_BY_TOPIC_RESULT",
                    correlationId,
                    data: messages,
                  }),
                },
              ],
            });
            break;
          }

          case "UPDATE": {
            const updated = await repository.update(
              Number(data.id),
              data.patch,
            );
            await producer.send({
              topic: "OutTopic",
              messages: [
                {
                  key: String(data.id),
                  value: JSON.stringify({
                    action: "UPDATE_RESULT",
                    correlationId,
                    data: updated ?? null,
                  }),
                },
              ],
            });
            break;
          }

          case "DELETE": {
            const ok = await repository.delete(Number(data.id));
            await producer.send({
              topic: "OutTopic",
              messages: [
                {
                  key: String(data.id),
                  value: JSON.stringify({
                    action: "DELETE_RESULT",
                    correlationId,
                    data: ok,
                  }),
                },
              ],
            });
            break;
          }

          default:
            console.warn(`[discussion] Unknown action: ${action}`);
        }
      } catch (err) {
        console.error(`[discussion] Error processing ${action}:`, err);
        await producer.send({
          topic: "OutTopic",
          messages: [
            {
              key: String(data?.id ?? "error"),
              value: JSON.stringify({
                action: `${action}_ERROR`,
                correlationId,
                error: (err as Error).message,
              }),
            },
          ],
        });
      }
    },
  });

  console.log("[discussion] Kafka consumer started");
}
