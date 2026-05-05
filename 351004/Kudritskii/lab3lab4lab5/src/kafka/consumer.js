const { Kafka } = require('kafkajs');
const kafka = new Kafka({ clientId: 'discussion', brokers: ['localhost:9092'] });
const consumer = kafka.consumer({ groupId: 'discussion-group' });


exports.runConsumer = async () => {
  await consumer.connect();
  await consumer.subscribe({ topic: 'notice-topic' });
};
