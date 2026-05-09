const { Kafka } = require('kafkajs');
const kafka = new Kafka({ clientId: 'discussion', brokers: ['localhost:9092'] });
const consumer = kafka.consumer({ groupId: 'discussion-group' });
const { getNote, createNote, updateNote, deleteNote } = require('../models/note');
const { moderate } = require('../moderation');
const { sendResponse } = require('./producer');

async function start() {
    await consumer.connect();
    await consumer.subscribe({ topic: 'InTopic', fromBeginning: true });
    await consumer.run({
        eachMessage: async ({ message }) => {
            const { correlationId, operation, payload } = JSON.parse(message.value.toString());
            let result;
            try {
                switch (operation) {
                    case 'CREATE': {
                        if (typeof payload.newsId !== 'number') {
                            result = { success: false, error: 'newsId must be a number' };
                            break;
                        }
                        const note = await createNote(payload);
                        const state = moderate(payload.content);
                        await updateNote(note.id, { state });
                        note.state = state;
                        result = { success: true, note };
                        break;
                    }
                    case 'READ': {
                        const note = await safeGetNote(payload.id);
                        result = note ? { success: true, note } : { success: false, error: 'Note not found' };
                        break;
                    }
                    case 'UPDATE': {
                        const existing = await safeGetNote(payload.id);
                        if (!existing) {
                            result = { success: false, error: 'Note not found' };
                            break;
                        }
                        await updateNote(payload.id, payload);
                        if (payload.content) {
                            const state = moderate(payload.content);
                            await updateNote(payload.id, { state });
                        }
                        const updated = await safeGetNote(payload.id);
                        result = { success: true, note: updated };
                        break;
                    }
                    case 'DELETE': {
                        const existing = await safeGetNote(payload.id);
                        if (!existing) {
                            result = { success: false, error: 'Note not found' };
                        } else {
                            await deleteNote(payload.id);
                            result = { success: true };
                        }
                        break;
                    }
                    default:
                        result = { success: false, error: 'Unknown operation' };
                }
            } catch (e) {
                result = { success: false, error: 'Note not found' };
            }
            await sendResponse(correlationId, result);
        }
    });
    console.log('Discussion consumer ready');
}

async function safeGetNote(id) {
    try {
        return await getNote(id);
    } catch (e) {
        return null;
    }
}

module.exports = { start };