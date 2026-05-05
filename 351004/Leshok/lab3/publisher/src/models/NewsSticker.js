const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
  return sequelize.define(
    'tbl_news_sticker',
    {
      newsId: { type: DataTypes.BIGINT, primaryKey: true },
      stickerId: { type: DataTypes.BIGINT, primaryKey: true },
    },
    { timestamps: false, tableName: 'tbl_news_sticker' }
  );
};