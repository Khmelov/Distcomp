const app = require('./src/app');
const { sequelize } = require('./src/models');

const PORT = process.env.PORT || 24110;

sequelize.authenticate()
  .then(() => {
    console.log('Database connected...');
    app.listen(PORT, () => {
      console.log(`Server running on http://localhost:${PORT}`);
    });
  })
  .catch(err => {
    console.error('Unable to connect to database:', err);
    process.exit(1);
  });