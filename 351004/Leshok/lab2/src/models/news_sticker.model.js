module.exports = (sequelize, DataTypes) => {
  const NewsSticker = sequelize.define('NewsSticker', {
    news_id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      field: 'news_id'   
    },
    sticker_id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      field: 'sticker_id'
    }
  }, {
    tableName: 'tbl_news_sticker',
    timestamps: false
  });
  return NewsSticker;
};