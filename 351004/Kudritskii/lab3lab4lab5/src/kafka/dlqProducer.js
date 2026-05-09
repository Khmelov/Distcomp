const { Kafka } = require("kafkajs");

const kafka = new Kafka({
  clientId: "dlq",
  brokers: ["localhost:9092"],
});

const producer = kafka.producer();

exports.sendToDLQ = async (message) => {
  await producer.connect();
  await producer.send({
    topic: "notice-dlq",
    messages: [{ value: JSON.stringify(message) }],
  });
};
