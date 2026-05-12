// server.js
require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const v1Routes = require('./routes/v1Routes');
const v2Routes = require('./routes/v2Routes');
const errorHandler = require('./utils/errorHandler');

const app = express();
const PORT = process.env.PORT || 24110;

app.use(bodyParser.json());

// Версии API
app.use('/api/v1.0', v1Routes);
app.use('/api/v2.0', v2Routes);

// Глобальный обработчик ошибок (должен быть последним middleware)
app.use(errorHandler);

// Запуск сервера
const server = app.listen(PORT, () => {
  console.log(`Сервер запущен на http://localhost:${PORT}`);
});

// Экспортируем для тестов
module.exports = { app, server };