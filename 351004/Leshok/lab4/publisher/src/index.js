const express = require('express');
const app = express();
app.use(express.json());

const { ensureTopics } = require('./kafka/admin');
const { startProducer } = require('./kafka/producer');
const { startConsumer } = require('./kafka/consumer');

const creatorsRouter = require('./routes/creators');
const newsRouter = require('./routes/news');
const stickersRouter = require('./routes/stickers');
const notesRouter = require('./routes/notes');

(async () => {
    try {
        await ensureTopics();
        await startProducer();
        await startConsumer();
        console.log('Kafka ready');
    } catch (e) {
        console.error('Failed to initialize Kafka', e);
        process.exit(1);
    }

    app.use('/api/v1.0/creators', creatorsRouter);
    app.use('/api/v1.0/news', newsRouter);
    app.use('/api/v1.0/stickers', stickersRouter);
    app.use('/api/v1.0/notes', notesRouter);

    app.listen(24110, () => console.log('Publisher running on http://localhost:24110'));
})();