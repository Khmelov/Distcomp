const { Kafka } = require("kafkajs");

const kafka = new Kafka({
  clientId: "publisher",
  brokers: ["localhost:9092"],
});

const producer = kafka.producer();

const connect = async () => {
  await producer.connect();
};

const sendNotice = async (notice) => {
  await producer.send({
    topic: "notice-topic",
    messages: [
      {
        key: String(notice.news_id),
        value: JSON.stringify(notice),
      },
    ],
  });
};

module.exports = { connect, sendNotice };
