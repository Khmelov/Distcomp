module.exports = (sequelize, DataTypes) => {
  return sequelize.define('Notice', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    content: DataTypes.TEXT,
  }, {
    tableName: 'tbl_notice',
    timestamps: false,
  });
};