const express = require('express');
const apiRoutes = require('./api');
const { errorHandler } = require('./errors');
const { sequelize } = require('./db');

const app = express();
app.use(express.json());
app.use('/api/v1.0', apiRoutes);
app.use(errorHandler);

async function startServer() {
    try {
        await sequelize.authenticate();
        await sequelize.sync({ alter: true });
        app.listen(24110, '0.0.0.0', () => console.log(`Publisher running on 24110`));
    } catch (error) { console.error('DB Error:', error); }
}
startServer();