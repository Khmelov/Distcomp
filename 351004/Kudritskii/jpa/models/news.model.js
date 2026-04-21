module.exports = (sequelize, DataTypes) => {
  return sequelize.define('News', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    title: DataTypes.STRING,
    content: DataTypes.TEXT,
  }, {
    tableName: 'tbl_news',
    timestamps: false,
  });
};