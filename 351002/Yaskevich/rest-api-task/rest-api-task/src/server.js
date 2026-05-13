const express = require('express');
const apiRoutes = require('./api');
const { errorHandler } = require('./errors');

const app = express();
app.use(express.json());

app.use('/api/v1.0', apiRoutes);

app.use(errorHandler);

const PORT = 24110;
if (require.main === module) {
    app.listen(PORT, '0.0.0.0', () => {
        console.log(`Server is running on http://localhost:${PORT}/api/v1.0`);
    });
}

module.exports = app;