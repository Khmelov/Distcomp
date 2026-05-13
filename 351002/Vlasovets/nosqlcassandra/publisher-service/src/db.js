const { Sequelize, DataTypes } = require('sequelize');

const DB_HOST = process.env.DB_HOST || 'localhost';
const sequelize = new Sequelize('distcomp', 'postgres', 'postgres', {
    host: DB_HOST, port: 5432, dialect: 'postgres', logging: false, define: { timestamps: false }
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
    editorId: { type: DataTypes.INTEGER, field: 'editor_id', allowNull: false }
}, { tableName: 'tbl_story' });

const Label = sequelize.define('Label', {
    id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
    name: { type: DataTypes.STRING(32), unique: true, allowNull: false }
}, { tableName: 'tbl_label' });

Editor.hasMany(Story, { foreignKey: 'editorId', onDelete: 'CASCADE' });
Story.belongsTo(Editor, { foreignKey: 'editorId' });

Story.belongsToMany(Label, { through: 'tbl_story_label', foreignKey: 'story_id', otherKey: 'label_id', timestamps: false });
Label.belongsToMany(Story, { through: 'tbl_story_label', foreignKey: 'label_id', otherKey: 'story_id', timestamps: false });

module.exports = { sequelize, Editor, Story, Label };