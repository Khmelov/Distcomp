const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const v1Routes = require('./routes/v1');
const errorHandler = require('./middlewares/errorHandler');

const app = express();

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use('/api/v1.0', v1Routes);

app.use(errorHandler);

module.exports = app;