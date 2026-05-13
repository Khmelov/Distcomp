const express = require('express');
const apiRoutes = require('./api');
const { errorHandler } = require('./errors');
const { sequelize } = require('./db');

const app = express();
app.use(express.json());

app.use('/api/v1.0', apiRoutes);
app.use(errorHandler);

const PORT = 24110;

async function startServer() {
    try {
        await sequelize.authenticate();
        console.log('Connection to PostgreSQL established successfully.');
        
        // Синхронизируем таблицы (создадутся в дефолтной схеме)
        await sequelize.sync({ alter: true });
        console.log('Database schema synced successfully.');

        app.listen(PORT, '0.0.0.0', () => {
            console.log(`Server is running on http://0.0.0.0:${PORT}/api/v1.0`);
        });
    } catch (error) {
        console.error('Unable to connect to the database:', error);
    }
}

startServer();