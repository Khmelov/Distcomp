module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.createTable('tbl_creator', {
      id: { type: Sequelize.INTEGER, autoIncrement: true, primaryKey: true },
      login: { type: Sequelize.STRING(64), allowNull: false, unique: true },
      password: { type: Sequelize.STRING(128), allowNull: false },
      firstname: { type: Sequelize.STRING(64), allowNull: false },
      lastname: { type: Sequelize.STRING(64), allowNull: false }
    });

    await queryInterface.createTable('tbl_sticker', {
      id: { type: Sequelize.INTEGER, autoIncrement: true, primaryKey: true },
      name: { type: Sequelize.STRING(32), allowNull: false, unique: true }
    });

    await queryInterface.createTable('tbl_news', {
      id: { type: Sequelize.INTEGER, autoIncrement: true, primaryKey: true },
      title: { type: Sequelize.STRING(128), allowNull: false },
      content: { type: Sequelize.TEXT, allowNull: false },
      created: { type: Sequelize.DATE, defaultValue: Sequelize.NOW },
      modified: { type: Sequelize.DATE, defaultValue: Sequelize.NOW },
      creator_id: { type: Sequelize.INTEGER, allowNull: false,
        references: { model: 'tbl_creator', key: 'id' },
        onDelete: 'CASCADE' }
    });

    await queryInterface.createTable('tbl_note', {
      id: { type: Sequelize.INTEGER, autoIncrement: true, primaryKey: true },
      content: { type: Sequelize.TEXT, allowNull: false },
      news_id: { type: Sequelize.INTEGER, allowNull: false,
        references: { model: 'tbl_news', key: 'id' },
        onDelete: 'CASCADE' }
    });

    await queryInterface.createTable('tbl_news_sticker', {
      news_id: {
        type: Sequelize.INTEGER,
        primaryKey: true,
        references: { model: 'tbl_news', key: 'id' },
        onDelete: 'CASCADE'
      },
      sticker_id: {
        type: Sequelize.INTEGER,
        primaryKey: true,
        references: { model: 'tbl_sticker', key: 'id' },
        onDelete: 'CASCADE'
      }
    });
  },

  down: async (queryInterface) => {
    await queryInterface.dropTable('tbl_news_sticker');
    await queryInterface.dropTable('tbl_note');
    await queryInterface.dropTable('tbl_news');
    await queryInterface.dropTable('tbl_sticker');
    await queryInterface.dropTable('tbl_creator');
  }
};