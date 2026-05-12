const { Sequelize, DataTypes } = require('sequelize');
const config = require('../config/database.js')[process.env.NODE_ENV || 'development'];
const sequelize = new Sequelize(config.database, config.username, config.password, config);

const Creator = require('./creator.model.js')(sequelize, DataTypes);
const News = require('./news.model.js')(sequelize, DataTypes);
const Sticker = require('./sticker.model.js')(sequelize, DataTypes);
const Note = require('./note.model.js')(sequelize, DataTypes);
const NewsSticker = require('./news_sticker.model.js')(sequelize, DataTypes);

const models = { Creator, News, Sticker, Note, NewsSticker };
Object.values(models).forEach(model => {
  if (model.associate) model.associate(models);
});

module.exports = { sequelize, ...models };