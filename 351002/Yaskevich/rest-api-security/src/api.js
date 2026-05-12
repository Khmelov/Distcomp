const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { AppError } = require('./errors');
const { Editor, Story, Label, Comment } = require('./db');

const JWT_SECRET = 'super-secret-jwt-key-for-task361';

// Универсальный маппер (убирает пароль из ответа для безопасности)
const mapEntity = (entity, modelName) => {
    const json = entity.toJSON();
    if (json.password) delete json.password; // Никогда не отдаем пароль по API!
    if (modelName === 'Story') {
        json.labels = json.Labels;
        json.labelIds = (json.Labels ||[]).map(l => l.id);
        delete json.Labels;
    }
    return json;
};

// --- МИДЛВАРЫ БЕЗОПАСНОСТИ ДЛЯ V2.0 ---
const authenticateJWT = (req, res, next) => {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return next(new AppError(401, 40100, "Missing or invalid Authorization header"));
    }
    const token = authHeader.split(' ')[1];
    jwt.verify(token, JWT_SECRET, (err, decoded) => {
        if (err) return next(new AppError(401, 40101, "Token expired or invalid"));
        req.user = decoded; // { sub: login, role, id }
        next();
    });
};

const authorizeAccess = (modelName) => {
    return async (req, res, next) => {
        try {
            if (req.user.role === 'ADMIN') return next(); // Админу можно всё
            if (req.method === 'GET') return next(); // Читать можно всё

            // Если CUSTOMER, проверяем права на редактирование/удаление
            const userId = req.user.id;
            const targetId = req.params.id || req.body.id;

            if (modelName === 'Label') throw new AppError(403, 40300, "Access denied"); // Метки правит только Админ

            if (modelName === 'Editor') {
                if (targetId && Number(targetId) !== Number(userId)) throw new AppError(403, 40300, "Access denied");
            }
            if (modelName === 'Story') {
                if (req.method === 'POST' && Number(req.body.editorId) !== Number(userId)) throw new AppError(403, 40300, "Access denied");
                if (['PUT', 'DELETE'].includes(req.method) && targetId) {
                    const story = await Story.findByPk(targetId);
                    if (story && Number(story.editorId) !== Number(userId)) throw new AppError(403, 40300, "Access denied");
                }
            }
            if (modelName === 'Comment') {
                if (['PUT', 'DELETE'].includes(req.method) && targetId) {
                    const comment = await Comment.findByPk(targetId);
                    if (comment) {
                        const story = await Story.findByPk(comment.storyId);
                        if (story && Number(story.editorId) !== Number(userId)) throw new AppError(403, 40300, "Access denied");
                    }
                }
            }
            next();
        } catch (e) { next(e); }
    };
};

// --- ОСНОВНОЙ ВАЛИДАТОР ---
function getValidator(modelName) {
    return async (req, res, next) => {
        try {
            if (['POST', 'PUT', 'PATCH'].includes(req.method)) {
                const body = req.body;
                if (!body || Object.keys(body).length === 0) throw new AppError(400, 40000, "Empty body");
                if (req.params.id && body.id && String(body.id) !== String(req.params.id)) throw new AppError(400, 40010, "ID mismatch");

                if (req.method !== 'PATCH') {
                    const reqFields = { Editor:['login','password','firstname','lastname'], Story:['title','content','editorId'], Label:['name'], Comment:['content','storyId'] }[modelName] || [];
                    for (const f of reqFields) {
                        if (body[f] === undefined || body[f] === null || body[f] === '') throw new AppError(400, 40005, `${f} is required`);
                    }
                }

                if (modelName === 'Editor' && body.login) {
                    const dup = await Editor.findOne({ where: { login: body.login } });
                    if (dup && dup.id !== Number(req.params.id || body.id)) throw new AppError(403, 40301, "Login must be unique");
                }
            }
            next();
        } catch (e) { next(e); }
    };
}

async function syncLabels(entity, req) {
    const labelsInput = req.body.labels || req.body.labelIds;
    if (labelsInput && Array.isArray(labelsInput)) {
        const oldLabels = await entity.getLabels();
        const oldLabelIds = oldLabels.map(l => l.id);
        const ids =[];
        for (const item of labelsInput) {
            if (typeof item === 'number') ids.push(item);
            else if (typeof item === 'string') { const [l] = await Label.findOrCreate({ where: { name: item } }); ids.push(l.id); }
            else if (typeof item === 'object') {
                if (item.id) { if (item.name) await Label.update({ name: item.name }, { where: { id: item.id } }); ids.push(item.id); } 
                else if (item.name) { const [l] = await Label.findOrCreate({ where: { name: item.name } }); ids.push(l.id); }
            }
        }
        await entity.setLabels(ids);
        const orphanedIds = oldLabelIds.filter(id => !ids.includes(id));
        if (orphanedIds.length > 0) await Label.destroy({ where: { id: orphanedIds } });
    }
}

// --- ФАБРИКА API (Генерирует роуты для v1 и v2) ---
function buildApi(version) {
    const router = express.Router();
    const isV2 = version === 'v2';

    // Эндпоинт авторизации (только для V2)
    if (isV2) {
        router.post('/login', async (req, res, next) => {
            try {
                const { login, password } = req.body;
                if (!login || !password) throw new AppError(400, 40000, "Login and password required");
                const editor = await Editor.findOne({ where: { login } });
                
                // Проверка пароля через BCrypt
                if (!editor || !await bcrypt.compare(password, editor.password)) {
                    throw new AppError(401, 40101, "Invalid credentials");
                }
                
                // Генерация JWT токена
                const token = jwt.sign(
                    { sub: editor.login, role: editor.role, id: editor.id }, // payload
                    JWT_SECRET,
                    { expiresIn: '24h' }
                );
                res.status(200).json({ access_token: token, token_type: "Bearer" });
            } catch (e) { next(e); }
        });
    }

    const bindCrud = (path, Model) => {
        const mName = Model.name;
        
        // Генерация обработчиков
        const handleReq = async (req, res, next, action) => {
            try {
                if (action === 'CREATE') {
                    const entity = await Model.create(req.body);
                    if (mName === 'Story') await syncLabels(entity, req);
                    res.status(201).json(mapEntity(await Model.findByPk(entity.id, { include: mName === 'Story' ? Label : undefined }), mName));
                } else if (action === 'GET_ALL') {
                    const items = await Model.findAll({ include: mName === 'Story' ? Label : undefined });
                    res.status(200).json(items.map(e => mapEntity(e, mName)));
                } else if (action === 'GET_BY_ID') {
                    const entity = await Model.findByPk(req.params.id, { include: mName === 'Story' ? Label : undefined });
                    if (!entity) throw new AppError(404, 40401, "Entity not found");
                    res.status(200).json(mapEntity(entity, mName));
                } else if (action === 'UPDATE') {
                    const id = req.params.id || req.body.id;
                    const entity = await Model.findByPk(id);
                    if (!entity) throw new AppError(404, 40401, "Entity not found");
                    await entity.update(req.body);
                    if (mName === 'Story') await syncLabels(entity, req);
                    res.status(200).json(mapEntity(await Model.findByPk(entity.id, { include: mName === 'Story' ? Label : undefined }), mName));
                } else if (action === 'DELETE') {
                    const id = req.params.id;
                    const deleted = await Model.destroy({ where: { id } });
                    if (!deleted) throw new AppError(404, 40401, "Entity not found");
                    res.status(204).send();
                }
            } catch (e) { next(e); }
        };

        const v = getValidator(mName);
        // Если это V2, накидываем мидлвары (Регистрация /editors открыта, остальное закрыто)
        const sec = isV2 ? [authenticateJWT, authorizeAccess(mName)] :[];
        const secCreate = (isV2 && mName === 'Editor') ? [] : sec;
        
        // Роуты
        router.post(path, [...secCreate, v], (req, res, next) => handleReq(req, res, next, 'CREATE'));
        router.get(path, [...sec], (req, res, next) => handleReq(req, res, next, 'GET_ALL')); // <-- Добавили защиту
        router.get(`${path}/:id`, [...sec], (req, res, next) => handleReq(req, res, next, 'GET_BY_ID')); // <-- Добавили защиту
        router.put(`${path}/:id`, [...sec, v], (req, res, next) => handleReq(req, res, next, 'UPDATE'));
        router.delete(`${path}/:id`, [...sec], (req, res, next) => handleReq(req, res, next, 'DELETE'));
    };

    bindCrud('/editors', Editor);
    bindCrud('/stories', Story);
    bindCrud('/labels', Label);
    bindCrud('/comments', Comment);

    return router;
}

// Экспортируем оба API
const apiV1 = buildApi('v1');
const apiV2 = buildApi('v2');

module.exports = { apiV1, apiV2 };