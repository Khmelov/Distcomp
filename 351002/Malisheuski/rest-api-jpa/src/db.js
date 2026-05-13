const { Sequelize, DataTypes } = require('sequelize');

const DB_HOST = process.env.DB_HOST || 'localhost';

const sequelize = new Sequelize('distcomp', 'postgres', 'postgres', {
    host: DB_HOST,
    port: 5432,
    dialect: 'postgres',
    logging: false,
    define: { timestamps: false }
});

const Editor = sequelize.define('Editor', {
    id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
    login: { type: DataTypes.STRING(32), unique: true, allowNull: false },
    password: { type: DataTypes.STRING(32), allowNull: false },
    firstname: { type: DataTypes.STRING(32), allowNull: false },
    lastname: { type: DataTypes.STRING(32), allowNull: false }
}, { tableName: 'tbl_editor' });

const Story = sequelize.define('Story', {
    id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
    title: { type: DataTypes.STRING(32), unique: true, allowNull: false },
    content: { type: DataTypes.TEXT, allowNull: false },
    // ВАЖНО: В JS это editorId, а в базе строго editor_id
    editorId: { type: DataTypes.INTEGER, field: 'editor_id', allowNull: false } 
}, { tableName: 'tbl_story' });

const Label = sequelize.define('Label', {
    id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
    name: { type: DataTypes.STRING(32), unique: true, allowNull: false }
}, { tableName: 'tbl_label' });

const Comment = sequelize.define('Comment', {
    id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
    content: { type: DataTypes.TEXT, allowNull: false },
    // ВАЖНО: В JS это storyId, а в базе строго story_id
    storyId: { type: DataTypes.INTEGER, field: 'story_id', allowNull: false }
}, { tableName: 'tbl_comment' });

// Связи
Editor.hasMany(Story, { foreignKey: 'editorId', onDelete: 'CASCADE' });
Story.belongsTo(Editor, { foreignKey: 'editorId' });

Story.hasMany(Comment, { foreignKey: 'storyId', onDelete: 'CASCADE' });
Comment.belongsTo(Story, { foreignKey: 'storyId' });

// Связь Многие-ко-Многим (в таблице tbl_story_label должны быть колонки story_id и label_id)
Story.belongsToMany(Label, { through: 'tbl_story_label', foreignKey: 'story_id', otherKey: 'label_id', timestamps: false });
Label.belongsToMany(Story, { through: 'tbl_story_label', foreignKey: 'label_id', otherKey: 'story_id', timestamps: false });

module.exports = { sequelize, Editor, Story, Label, Comment };