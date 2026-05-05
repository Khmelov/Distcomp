const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
  return sequelize.define(
    'tbl_sticker',
    {
      id: { type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true },
      name: { type: DataTypes.STRING(32), allowNull: false, unique: true, validate: { len: [2, 32] } },
    },
    { timestamps: false, tableName: 'tbl_sticker' }
  );
};