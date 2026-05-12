const express = require('express');
const app = express();
app.use(express.json());

const { init } = require('./models/note');
const { start: startProducer } = require('./kafka/producer');
const { start: startConsumer } = require('./kafka/consumer');
const notesRouter = require('./routes/notes');

(async () => {
    try {
        await init();               
        await startProducer();      
        await startConsumer();      
        console.log('Discussion service started');
    } catch (e) {
        console.error(e);
        process.exit(1);
    }

    app.use('/api/v1.0/notes', notesRouter);
    app.listen(24130, () => console.log('Discussion REST on http://localhost:24130'));
})();