import { Kafka } from "kafkajs";

export const kafka = new Kafka({
  clientId: "discussion-service",
  brokers: ["localhost:9092"],
});
