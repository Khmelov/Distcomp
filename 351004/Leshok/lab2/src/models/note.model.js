module.exports = (sequelize, DataTypes) => {
  const Note = sequelize.define('Note', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    content: {
      type: DataTypes.TEXT,
      allowNull: false,
      validate: {
        len: {
          args: [2, 2048],
          msg: 'Content must be between 2 and 2048 characters'
        },
        notNull: { msg: 'Content is required' }
      }
    },
    newsId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      field: 'news_id',
      validate: {
        notNull: { msg: 'newsId is required' }
      }
    }
  }, {
    tableName: 'tbl_note',
    timestamps: false
  });

  Note.associate = (models) => {
    Note.belongsTo(models.News, { foreignKey: 'newsId' });
  };

  return Note;
};