const { Sequelize } = require('sequelize');
const config = require('./src/config/database.js')[process.env.NODE_ENV || 'development'];

const sequelize = new Sequelize(config.database, config.username, config.password, config);

async function removeDuplicates() {
  try {
    console.log('Connecting to database...');
    await sequelize.authenticate();
    console.log('Connection established.');

    // Удаляем дубликаты, оставляя только одну запись с минимальным id для каждого title
    const [result] = await sequelize.query(`
      DELETE FROM tbl_news
      WHERE id NOT IN (
        SELECT MIN(id)
        FROM tbl_news
        GROUP BY title
      );
    `);
    console.log('Duplicates removed successfully.');
    process.exit(0);
  } catch (err) {
    console.error('Error removing duplicates:', err);
    process.exit(1);
  }
}

removeDuplicates();