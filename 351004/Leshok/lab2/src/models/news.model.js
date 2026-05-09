module.exports = (sequelize, DataTypes) => {
  const News = sequelize.define('News', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    title: {
      type: DataTypes.STRING(128),
      allowNull: false,
      unique: true,
      validate: {
        len: {
          args: [2, 128],
          msg: 'Title must be between 2 and 128 characters'
        },
        notNull: { msg: 'Title is required' }
      }
    },
content: {
  type: DataTypes.TEXT,
  allowNull: false,
  validate: {
    isString(value) {
      if (typeof value !== 'string') {
        throw new Error('Content must be a string');
      }
    },
    len: {
      args: [2, 4096],
      msg: 'Content must be between 2 and 4096 characters'
    },
    notNull: { msg: 'Content is required' }
  }
},
    created: { type: DataTypes.DATE, defaultValue: DataTypes.NOW },
    modified: { type: DataTypes.DATE, defaultValue: DataTypes.NOW },
    creatorId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      field: 'creator_id',
      validate: {
        notNull: { msg: 'creatorId is required' }
      }
    }
  }, {
    tableName: 'tbl_news',
    timestamps: false
  });

  News.associate = (models) => {
  News.belongsTo(models.Creator, { foreignKey: 'creatorId' });
  News.hasMany(models.Note, { foreignKey: 'newsId', onDelete: 'CASCADE' });
  News.belongsToMany(models.Sticker, {
    through: models.NewsSticker,
    foreignKey: 'news_id',    
    otherKey: 'sticker_id'    
  });
};

  return News;
};