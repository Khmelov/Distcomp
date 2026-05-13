const { Kafka } = require('kafkajs');
const kafka = new Kafka({ clientId: 'discussion', brokers: ['localhost:9092'] });
const producer = kafka.producer();

async function start() {
    await producer.connect();
    console.log('Discussion producer ready');
}

async function sendResponse(correlationId, result) {
    await producer.send({
        topic: 'OutTopic',
        messages: [{
            key: correlationId,
            value: JSON.stringify({ correlationId, ...result }),
            headers: { correlationId }
        }]
    });
}

module.exports = { start, sendResponse };