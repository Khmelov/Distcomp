const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
  return sequelize.define(
    'tbl_news',
    {
      id: { type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true },
      creatorId: { type: DataTypes.BIGINT, allowNull: false },
      title: { type: DataTypes.STRING(64), allowNull: false, validate: { len: [2, 64] } },
      content: { type: DataTypes.STRING(2048), allowNull: false, validate: { len: [4, 2048] } },
      created: { type: DataTypes.DATE, defaultValue: DataTypes.NOW },
      modified: { type: DataTypes.DATE, defaultValue: DataTypes.NOW },
    },
    { timestamps: false, tableName: 'tbl_news' }
  );
};