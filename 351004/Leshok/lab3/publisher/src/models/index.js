const { Sequelize } = require('sequelize');

const sequelize = new Sequelize(
  process.env.DB_NAME,
  process.env.DB_USER,
  process.env.DB_PASSWORD,
  {
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    dialect: 'postgres',
    logging: false,
  }
);

const Creator = require('./Creator')(sequelize);
const News = require('./News')(sequelize);
const Sticker = require('./Sticker')(sequelize);
const NewsSticker = require('./NewsSticker')(sequelize);

Creator.hasMany(News, { foreignKey: 'creatorId', onDelete: 'CASCADE' });
News.belongsTo(Creator, { foreignKey: 'creatorId' });

News.belongsToMany(Sticker, { through: NewsSticker, foreignKey: 'newsId', otherKey: 'stickerId' });
Sticker.belongsToMany(News, { through: NewsSticker, foreignKey: 'stickerId', otherKey: 'newsId' });

module.exports = { sequelize, Creator, News, Sticker, NewsSticker };