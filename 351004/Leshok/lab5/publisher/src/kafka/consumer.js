const { kafka } = require('./admin');

let consumer;
const pending = new Map();

async function startConsumer() {
    consumer = kafka.consumer({ groupId: 'publisher-response-group' });
    await consumer.connect();
    await consumer.subscribe({ topic: 'OutTopic', fromBeginning: true });
    await consumer.run({
        eachMessage: async ({ message }) => {
            const data = JSON.parse(message.value.toString());
            const { correlationId } = data;
            if (pending.has(correlationId)) {
                const handler = pending.get(correlationId);
                clearTimeout(handler.timer);
                pending.delete(correlationId);
                handler.resolve(data);
            }
        }
    });
}

function waitForResponse(correlationId) {
    return new Promise((resolve, reject) => {
        const timer = setTimeout(() => {
            pending.delete(correlationId);
            reject(new Error('Timeout waiting for Kafka response'));
        }, 1000);
        pending.set(correlationId, { resolve, reject, timer });
    });
}

module.exports = { startConsumer, waitForResponse };