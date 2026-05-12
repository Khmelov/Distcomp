const express = require('express');
const apiRoutes = require('./api');
const { errorHandler } = require('./errors');
const { sequelize } = require('./db');

const app = express();
app.use(express.json());

// Подключаем маршруты
app.use('/api/v1.0', apiRoutes);
app.use('/api/v2.0', apiRoutes); // На всякий случай для других тестов

app.use(errorHandler);

async function startServer() {
    try {
        await sequelize.authenticate();
        await sequelize.sync({ alter: true });
        console.log('Database synced.');

        // Запускаем сервер СРАЗУ на двух портах
        const ports = [24110, 24130];
        
        ports.forEach(port => {
            app.listen(port, '0.0.0.0', () => {
                console.log(`Server is running on port ${port}`);
            });
        });

    } catch (error) {
        console.error('Unable to connect to the database:', error);
    }
}

startServer();