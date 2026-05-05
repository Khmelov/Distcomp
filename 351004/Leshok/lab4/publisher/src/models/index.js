const Creator = require('./Creator')(sequelize);
const News = require('./News')(sequelize);
const Sticker = require('./Sticker')(sequelize);
const NewsSticker = require('./NewsSticker')(sequelize);

Creator.hasMany(News, { foreignKey: 'creatorId', onDelete: 'CASCADE' });
News.belongsTo(Creator, { foreignKey: 'creatorId' });

News.belongsToMany(Sticker, { through: NewsSticker, foreignKey: 'newsId', otherKey: 'stickerId' });
Sticker.belongsToMany(News, { through: NewsSticker, foreignKey: 'stickerId', otherKey: 'newsId' });