'use strict';

module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.addConstraint('tbl_news', {
      fields: ['title'],
      type: 'unique',
      name: 'unique_news_title'
    });
  },

  down: async (queryInterface, Sequelize) => {
    await queryInterface.removeConstraint('tbl_news', 'unique_news_title');
  }
};