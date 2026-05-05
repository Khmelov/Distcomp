const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
  return sequelize.define(
    'tbl_creator',
    {
      id: { type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true },
      login: { type: DataTypes.STRING(64), allowNull: false, validate: { len: [2, 64] } },
      password: { type: DataTypes.STRING(128), allowNull: false, validate: { len: [8, 128] } },
      firstname: { type: DataTypes.STRING(64), allowNull: false, validate: { len: [2, 64] } },
      lastname: { type: DataTypes.STRING(64), allowNull: false, validate: { len: [2, 64] } },
    },
    { timestamps: false, tableName: 'tbl_creator' }
  );
};