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

const PORT1 = 24110;
const PORT2 = 24130;

async function start() {
    try {
        await sequelize.sync({ alter: true });
        
        // Запускаем сервер на первом порту
        app.listen(PORT1, '0.0.0.0', () => {
            console.log(`Server 1 running on port ${PORT1}`);
        });

        // Запускаем сервер на втором порту (для тестов 24130)
        app.listen(PORT2, '0.0.0.0', () => {
            console.log(`Server 2 running on port ${PORT2}`);
        });
    } catch (e) {
        console.error(e);
    }
}

start();