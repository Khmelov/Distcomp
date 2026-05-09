const { Kafka } = require('kafkajs');

const kafka = new Kafka({ clientId: 'publisher', brokers: ['localhost:9092'] });
const admin = kafka.admin();

async function ensureTopics() {
    await admin.connect();
    const existing = await admin.listTopics();
    const needed = ['InTopic', 'OutTopic'];
    const toCreate = needed.filter(t => !existing.includes(t));
    if (toCreate.length) {
        await admin.createTopics({
            topics: toCreate.map(topic => ({ topic, numPartitions: 3, replicationFactor: 1 }))
        });
        console.log(`Created topics: ${toCreate.join(', ')}`);
    }
    await admin.disconnect();
}

module.exports = { ensureTopics, kafka };