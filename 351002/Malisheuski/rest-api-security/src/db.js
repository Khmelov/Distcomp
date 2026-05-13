const { Sequelize, DataTypes } = require('sequelize');
const bcrypt = require('bcryptjs');
const pg = require('pg');

// ВАЖНО: Указываем драйверу PG преобразовывать BIGINT в обычные числа JS, 
// чтобы JSON возвращал id: 1, а не id: "1" (требование тестов).
pg.defaults.parseInt8 = true;

const DB_HOST = process.env.DB_HOST || 'localhost';
const sequelize = new Sequelize('distcomp', 'postgres', 'postgres', {
    host: DB_HOST, port: 5432, dialect: 'postgres', logging: false, define: { timestamps: false }
});

const Editor = sequelize.define('Editor', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    login: { type: DataTypes.STRING(32), unique: true, allowNull: false },
    password: { type: DataTypes.STRING(255), allowNull: false }, // Длина 255 для хранения хэша BCrypt
    firstname: { type: DataTypes.STRING(32), allowNull: false },
    lastname: { type: DataTypes.STRING(32), allowNull: false },
    role: { type: DataTypes.STRING(32), defaultValue: 'CUSTOMER' } // Новое поле для ролей
}, { 
    tableName: 'tbl_editor',
    hooks: {
        // Хук Sequelize: Автоматически хэширует пароль перед сохранением
        beforeCreate: async (editor) => {
            if (editor.password) editor.password = await bcrypt.hash(editor.password, 10);
        },
        beforeUpdate: async (editor) => {
            if (editor.changed('password')) editor.password = await bcrypt.hash(editor.password, 10);
        }
    }
});

const Story = sequelize.define('Story', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    title: { type: DataTypes.STRING(32), unique: true, allowNull: false },
    content: { type: DataTypes.TEXT, allowNull: false },
    editorId: { type: DataTypes.BIGINT, field: 'editor_id', allowNull: false }
}, { tableName: 'tbl_story' });

const Label = sequelize.define('Label', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    name: { type: DataTypes.STRING(32), unique: true, allowNull: false }
}, { tableName: 'tbl_label' });

const Comment = sequelize.define('Comment', {
    id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
    content: { type: DataTypes.TEXT, allowNull: false },
    storyId: { type: DataTypes.BIGINT, field: 'story_id', allowNull: false }
}, { tableName: 'tbl_comment' });

Editor.hasMany(Story, { foreignKey: 'editorId', onDelete: 'CASCADE' });
Story.belongsTo(Editor, { foreignKey: 'editorId' });

Story.hasMany(Comment, { foreignKey: 'storyId', onDelete: 'CASCADE' });
Comment.belongsTo(Story, { foreignKey: 'storyId' });

Story.belongsToMany(Label, { through: 'tbl_story_label', foreignKey: 'story_id', otherKey: 'label_id', timestamps: false });
Label.belongsToMany(Story, { through: 'tbl_story_label', foreignKey: 'label_id', otherKey: 'story_id', timestamps: false });

module.exports = { sequelize, Editor, Story, Label, Comment };