module.exports = (sequelize, DataTypes) => {
  const Sticker = sequelize.define('Sticker', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    name: {
      type: DataTypes.STRING(32),
      allowNull: false,
      unique: true,
      validate: {
        len: {
          args: [2, 32],
          msg: 'Sticker name must be between 2 and 32 characters'
        },
        notNull: { msg: 'Name is required' }
      }
    }
  }, {
    tableName: 'tbl_sticker',
    timestamps: false
  });

  Sticker.associate = (models) => {
  Sticker.belongsToMany(models.News, {
    through: models.NewsSticker,
    foreignKey: 'sticker_id', 
    otherKey: 'news_id'      
  });
};

  return Sticker;
};