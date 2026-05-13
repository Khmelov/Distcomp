const express = require('express');
const app = express();
app.use(express.json());

const { ensureTopics } = require('./kafka/admin');
const { startProducer } = require('./kafka/producer');
const { startConsumer } = require('./kafka/consumer');
const { init: initDb } = require('./db');
const redisClient = require('./redisClient'); 

const creatorsRouter = require('./routes/creators');
const newsRouter = require('./routes/news');
const stickersRouter = require('./routes/stickers');
const notesRouter = require('./routes/notes');

(async () => {
  try {
    await initDb();             
    await redisClient.set('ping', 'pong'); 
    await ensureTopics();
    await startProducer();
    await startConsumer();
    console.log('Publisher ready (Postgres + Redis + Kafka)');
  } catch (e) {
    console.error('Failed to initialize:', e);
    process.exit(1);
  }

  app.use('/api/v1.0/creators', creatorsRouter);
  app.use('/api/v1.0/news', newsRouter);
  app.use('/api/v1.0/stickers', stickersRouter);
  app.use('/api/v1.0/notes', notesRouter);

  app.listen(24110, () => console.log('Publisher running on http://localhost:24110'));
})();