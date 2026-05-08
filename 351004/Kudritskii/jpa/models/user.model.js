module.exports = (sequelize, DataTypes) => {
  return sequelize.define('User', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    login: { type: DataTypes.STRING, allowNull: false },
    password: { type: DataTypes.STRING, allowNull: false },
  }, {
    tableName: 'tbl_user',
    timestamps: false,
  });
};