const { kafka } = require('./admin');
const { v4: uuidv4 } = require('uuid');

let producer;

async function startProducer() {
    producer = kafka.producer({ allowAutoTopicCreation: false });
    await producer.connect();
}

async function sendRequest(operation, noteData, newsId) {
    const correlationId = uuidv4();
    await producer.send({
        topic: 'InTopic',
        messages: [{
            key: String(newsId),  
            value: JSON.stringify({ correlationId, operation, payload: noteData }),
            headers: { correlationId }
        }]
    });
    return correlationId;
}

module.exports = { startProducer, sendRequest };