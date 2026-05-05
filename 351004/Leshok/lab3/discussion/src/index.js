require('dotenv').config();
const express = require('express');
const { connect } = require('./config/cassandra');
const noteController = require('./controllers/NoteController');
const errorHandler = require('./utils/errorHandler');

const app = express();
app.use(express.json());

app.post('/api/v1.0/notes', noteController.create);
app.put('/api/v1.0/notes/:id', noteController.update);
app.get('/api/v1.0/notes/:id', noteController.getOne);
app.get('/api/v1.0/notes', noteController.getAll);
app.delete('/api/v1.0/notes/:id', noteController.delete);

app.use(errorHandler);

const PORT = process.env.PORT || 24130;
async function start() {
  await connect();
  app.listen(PORT, () => console.log(`Discussion running on port ${PORT}`));
}
start();