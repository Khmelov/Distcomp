const Sequelize = require("sequelize");
const sequelize = require("../config/db");

const User = require("./user.model")(sequelize, Sequelize);
const News = require("./news.model")(sequelize, Sequelize);
const Label = require("./label.model")(sequelize, Sequelize);
const Notice = require("./notice.model")(sequelize, Sequelize);

// 1:M User -> News
User.hasMany(News);
News.belongsTo(User);

// 1:M News -> Notice
News.hasMany(Notice);
Notice.belongsTo(News);

// M:N News <-> Label
News.belongsToMany(Label, { through: "tbl_news_label" });
Label.belongsToMany(News, { through: "tbl_news_label" });

module.exports = {
  sequelize,
  User,
  News,
  Label,
  Notice,
};
