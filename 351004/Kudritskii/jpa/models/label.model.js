module.exports = (sequelize, DataTypes) => {
  return sequelize.define(
    "Label",
    {
      id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
      name: DataTypes.STRING,
    },
    {
      tableName: "tbl_label",
      timestamps: false,
    },
  );
};
