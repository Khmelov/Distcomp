const { Sequelize, DataTypes, Op } = require('sequelize');
const pg = require('pg');

pg.types.setTypeParser(20, (val) => {
    return parseInt(val, 10);
});
const sequelize = new Sequelize('distcomp', 'postgres', 'postgres', {
    host: process.env.DB_HOST || 'localhost',
    port: 5432,
    dialect: 'postgres',
    logging: false,
    define: { timestamps: false }
});

const Creator = sequelize.define('Creator', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    login: { type: DataTypes.STRING(64), unique: true, allowNull: false },
    password: { type: DataTypes.STRING(128), allowNull: false },
    firstname: { type: DataTypes.STRING(64), allowNull: false },
    lastname: { type: DataTypes.STRING(64), allowNull: false }
}, { tableName: 'tbl_creator' });

const Topic = sequelize.define('Topic', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    title: { type: DataTypes.STRING(64), allowNull: false },
    content: { type: DataTypes.TEXT, allowNull: false },
    creatorId: { type: DataTypes.BIGINT, field: 'creator_id', allowNull: false },
    created: { type: DataTypes.DATE, defaultValue: Sequelize.NOW },
    modified: { type: DataTypes.DATE, defaultValue: Sequelize.NOW }
}, { tableName: 'tbl_topic' });

const Mark = sequelize.define('Mark', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    name: { type: DataTypes.STRING(32), unique: true, allowNull: false }
}, { tableName: 'tbl_mark' });

const Post = sequelize.define('Post', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    content: { type: DataTypes.TEXT, allowNull: false },
    topicId: { type: DataTypes.BIGINT, field: 'topic_id', allowNull: false }
}, { tableName: 'tbl_post' });

// Связи
Creator.hasMany(Topic, { foreignKey: 'creatorId', onDelete: 'CASCADE' });
Topic.belongsTo(Creator, { foreignKey: 'creatorId' });

Topic.hasMany(Post, { foreignKey: 'topicId', onDelete: 'CASCADE' });
Post.belongsTo(Topic, { foreignKey: 'topicId' });

Topic.belongsToMany(Mark, { 
    through: 'tbl_topic_mark', 
    foreignKey: 'topic_id', 
    otherKey: 'mark_id', 
    as: 'marks' // Обязательно добавьте этот алиас!
});
Mark.belongsToMany(Topic, { 
    through: 'tbl_topic_mark', 
    foreignKey: 'mark_id', 
    otherKey: 'topic_id', 
    as: 'topics' 
});
module.exports = { sequelize, Creator, Topic, Mark, Post, Op };
