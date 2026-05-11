const express = require('express');
const { Creator, Topic, Mark, Post } = require('./db');
const { AppError, errorHandler } = require('./errors');

const router = express.Router();

function getValidator(modelName) {
    return async (req, res, next) => {
        try {
            if (['POST', 'PUT'].includes(req.method)) {
                const body = req.body;
                if (!body || Object.keys(body).length === 0) {
                    throw new AppError(400, 40000, "Empty request body");
                }

                const config = {
                    creator: {
                        fields: ['login', 'password', 'firstname', 'lastname'],
                        lengths: { login: [2, 64], password: [8, 128], firstname: [2, 64], lastname: [2, 64] }
                    },
                    topic: {
                        fields: ['title', 'content', 'creatorId'],
                        lengths: { title: [2, 64], content: [4, 2048] }
                    },
                    mark: {
                        fields: ['name'],
                        lengths: { name: [2, 32] }
                    },
                    post: {
                        fields: ['content', 'topicId'],
                        lengths: { content: [2, 2048] }
                    }
                };

                const currentModelName = modelName.toLowerCase();
                const currentConfig = config[currentModelName];
                if (!currentConfig) return next();

                // 1. Проверка обязательных полей (только POST)
                if (req.method === 'POST') {
                    for (const f of currentConfig.fields) {
                        if (body[f] === undefined || body[f] === null || body[f] === '') {
                            throw new AppError(400, 40005, `${f} is required`);
                        }
                    }
                }

                // 2. Валидация длины строк
                for (const [field, [min, max]] of Object.entries(currentConfig.lengths)) {
                    const value = body[field];
                    if (value !== undefined && value !== null) {
                        const strValue = String(value);
                        if (strValue.length < min || strValue.length > max) {
                            throw new AppError(400, 40003, `${field} length must be between ${min} and ${max}`);
                        }
                    }
                }

                // 3. ПРОВЕРКА НА ДУБЛИКАТЫ (чтобы получить 403, если БД не настроена)
                if (currentModelName === 'creator' && body.login) {
                    const existing = await Creator.findOne({ where: { login: body.login } });
                    // Если нашли такой логин и это POST (новый) или PUT (но ID другой)
                    if (existing && (req.method === 'POST' || existing.id != req.params.id)) {
                        throw new AppError(403, 40305, "Login already exists");
                    }
                }

                if (currentModelName === 'topic' && body.title) {
                    const existing = await Topic.findOne({ where: { title: body.title } });
                    if (existing && (req.method === 'POST' || existing.id != req.params.id)) {
                        throw new AppError(403, 40306, "Title already exists");
                    }
                }

                // 4. ПРОВЕРКА ВНЕШНИХ КЛЮЧЕЙ (Foreign Keys)
                if (body.creatorId) {
                    const exists = await Creator.findByPk(body.creatorId);
                    if (!exists) throw new AppError(403, 40301, "Creator not found");
                }
                if (body.topicId) {
                    const exists = await Topic.findByPk(body.topicId);
                    if (!exists) throw new AppError(403, 40302, "Topic not found");
                }
            }
            next();
        } catch (e) {
            next(e);
        }
    };
}

function createCrud(Model) {
    return {
        create: async (req, res, next) => {
            try {
                // 1. Создаем основную сущность (Topic, Creator и т.д.)
                // Извлекаем marks, чтобы они не мешали стандартному созданию
                const { marks, ...bodyData } = req.body;
                const entity = await Model.create(bodyData);

                // 2. Если это Topic и есть метки, обрабатываем их
                if (Model.name === 'Topic' && marks && Array.isArray(marks)) {
                    const markInstances = await Promise.all(
                        marks.map(m => {
                            // Проверяем: метка это строка или объект
                            const name = typeof m === 'string' ? m : m.name;
                            return Mark.findOrCreate({ where: { name: name } });
                        })
                    );
                    // Привязываем найденные/созданные метки к топику
                    // markInstances — это массив [instance, created], берем [0]
                    await entity.setMarks(markInstances.map(m => m[0]));
                }

                // 3. Возвращаем результат с подгруженными метками
                const result = await Model.findByPk(entity.id, {
                    include: Model.name === 'Topic' ? { model: Mark, as: 'marks' } : undefined
                });
                
                res.status(201).json(result);
            } catch (e) {
                if (e.name === 'SequelizeUniqueConstraintError') {
                    return next(new AppError(403, 40300, e.errors[0].message));
                }
                next(e);
            }
        },

        update: async (req, res, next) => {
            try {
                const entity = await Model.findByPk(req.params.id);
                if (!entity) throw new AppError(404, 40401, "Not found");

                const { marks, ...bodyData } = req.body;
                await entity.update(bodyData);

                if (Model.name === 'Topic' && marks && Array.isArray(marks)) {
                    const markInstances = await Promise.all(
                        marks.map(m => {
                            const name = typeof m === 'string' ? m : m.name;
                            return Mark.findOrCreate({ where: { name: name } });
                        })
                    );
                    await entity.setMarks(markInstances.map(m => m[0]));
                    
                    // ОЧИСТКА: Удаляем метки, которые больше не связаны ни с одним топиком
                    await Mark.sequelize.query(`
                        DELETE FROM tbl_mark 
                        WHERE id NOT IN (SELECT mark_id FROM tbl_topic_mark)
                    `);
                }

                const result = await Model.findByPk(entity.id, {
                    include: Model.name === 'Topic' ? { model: Mark, as: 'marks' } : undefined
                });
                res.status(200).json(result);
            } catch (e) {
                if (e.name === 'SequelizeUniqueConstraintError') {
                    return next(new AppError(403, 40300, e.errors[0].message));
                }
                next(e);
            }
        },

        
        // Остальные методы (getAll, getById, delete) остаются такими же, 
        // но убедитесь, что в include используется { model: Mark, as: 'marks' }
        getAll: async (req, res, next) => {
            try {
                const { limit, offset, sort, order } = req.query;
                const items = await Model.findAll({
                    limit: limit ? parseInt(limit) : undefined,
                    offset: offset ? parseInt(offset) : undefined,
                    order: sort ? [[sort, order || 'ASC']] : undefined,
                    include: Model.name === 'Topic' ? { model: Mark, as: 'marks' } : undefined
                });
                res.status(200).json(items);
            } catch (e) { next(e); }
        },
        getById: async (req, res, next) => {
            try {
                const entity = await Model.findByPk(req.params.id, {
                    include: Model.name === 'Topic' ? { model: Mark, as: 'marks' } : undefined
                });
                if (!entity) throw new AppError(404, 40401, "Not found");
                res.status(200).json(entity);
            } catch (e) { next(e); }
        },
        delete: async (req, res, next) => {
            try {
                const id = req.params.id;
                const entity = await Model.findByPk(id);
                if (!entity) throw new AppError(404, 40401, "Not found");

                await entity.destroy();

                // ОЧИСТКА: Если удалили топик или саму метку напрямую, 
                // проверяем, не остались ли в tbl_mark записи без связей
                if (Model.name === 'Topic' || Model.name === 'Mark') {
                    await Mark.sequelize.query(`
                        DELETE FROM tbl_mark 
                        WHERE id NOT IN (SELECT mark_id FROM tbl_topic_mark)
                    `);
                }

                res.status(204).send();
            } catch (e) { 
                next(e); 
            }
        }
    };
}

const bind = (path, Model) => {
    const crud = createCrud(Model);
    // Передаем имя модели для валидатора
    const val = getValidator(Model.name);
    router.post(path, val, crud.create);
    router.get(path, crud.getAll);
    router.get(`${path}/:id`, crud.getById);
    router.put(`${path}/:id`, val, crud.update);
    router.delete(`${path}/:id`, crud.delete);
};

bind('/creators', Creator);
bind('/topics', Topic);
bind('/marks', Mark);
bind('/posts', Post);

// Использование вашего обработчика ошибок
router.use(errorHandler);

module.exports = router;