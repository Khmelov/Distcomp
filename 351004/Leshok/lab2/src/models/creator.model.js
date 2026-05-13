module.exports = (sequelize, DataTypes) => {
  const Creator = sequelize.define('Creator', {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    login: {
      type: DataTypes.STRING(64),
      allowNull: false,
      unique: true,
      validate: {
        len: {
          args: [2, 64],
          msg: 'Login must be between 2 and 64 characters'
        },
        notNull: { msg: 'Login is required' }
      }
    },
    password: {
      type: DataTypes.STRING(128),
      allowNull: false,
      validate: {
        len: {
          args: [8, 128],
          msg: 'Password must be at least 8 characters'
        },
        notNull: { msg: 'Password is required' }
      }
    },
    firstname: {
      type: DataTypes.STRING(64),
      allowNull: false,
      validate: {
        len: {
          args: [2, 64],
          msg: 'Firstname must be between 2 and 64 characters'
        },
        notNull: { msg: 'Firstname is required' }
      }
    },
    lastname: {
      type: DataTypes.STRING(64),
      allowNull: false,
      validate: {
        len: {
          args: [2, 64],
          msg: 'Lastname must be between 2 and 64 characters'
        },
        notNull: { msg: 'Lastname is required' }
      }
    }
  }, {
    tableName: 'tbl_creator',
    timestamps: false
  });

  Creator.associate = (models) => {
    Creator.hasMany(models.News, { foreignKey: 'creatorId', onDelete: 'CASCADE' });
  };

  return Creator;
};