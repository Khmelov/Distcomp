const express = require('express');
const { apiV1, apiV2 } = require('./src/api');
const { errorHandler } = require('./src/errors');
const { sequelize } = require('./src/db');

const app = express();
app.use(express.json());

// Подключаем v1 (открытый) и v2 (защищенный)
app.use('/api/v1.0', apiV1);
app.use('/api/v2.0', apiV2);

app.use(errorHandler);

const PORT = 24110;
sequelize.sync({ alter: true }).then(() => {
    app.listen(PORT, '0.0.0.0', () => console.log(`Server running on port ${PORT}`));
});