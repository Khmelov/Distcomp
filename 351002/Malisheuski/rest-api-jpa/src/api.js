const express = require('express');
const { AppError } = require('./errors');
const { Editor, Story, Label, Comment } = require('./db');
const router = express.Router();

function getValidator(modelName) {
    return async (req, res, next) => {
        try {
            if (['POST', 'PUT'].includes(req.method)) {
                const body = req.body;
                if (!body || Object.keys(body).length === 0) throw new AppError(400, 40000, "Empty request body");

                const pathId = req.params.id;
                const bodyId = body.id;
                if (pathId && bodyId !== undefined && String(bodyId) !== String(pathId)) {
                    throw new AppError(400, 40010, "ID in body does not match URL");
                }

                const requiredFields = {
                    Editor: ['login', 'password', 'firstname', 'lastname'],
                    Story: ['title', 'content', 'editorId'],
                    Label: ['name'],
                    Comment: ['content', 'storyId']
                };
                for (const f of (requiredFields[modelName] || [])) {
                    if (body[f] === undefined || body[f] === null || body[f] === '') {
                        throw new AppError(400, 40005, `${f} is required`);
                    }
                }

                for (const [key, value] of Object.entries(body)) {
                    if (value === null && key !== 'id') throw new AppError(400, 40001, `${key} cannot be null`);
                    if (['login', 'password', 'firstname', 'lastname', 'title', 'content', 'name'].includes(key)) {
                        if (typeof value !== 'string') throw new AppError(400, 40002, `${key} must be a string`);
                        const len = value.trim().length;
                        if (len < 2) throw new AppError(400, 40003, `${key} is too short`);
                        if (key !== 'content' && len > 32) throw new AppError(400, 40004, `${key} is too long`);
                        if (key === 'password' && len < 8) throw new AppError(400, 40006, `Password is too short`);
                    }
                }

                if (modelName === 'Story' && body.editorId !== undefined) {
                    if (!(await Editor.findByPk(body.editorId))) throw new AppError(400, 40012, "Editor not found");
                }
                if (modelName === 'Comment' && body.storyId !== undefined) {
                    if (!(await Story.findByPk(body.storyId))) throw new AppError(400, 40013, "Story not found");
                }

                const currentId = pathId ? Number(pathId) : (bodyId ? Number(bodyId) : null);
                if (modelName === 'Editor' && body.login) {
                    const dup = await Editor.findOne({ where: { login: body.login } });
                    if (dup && dup.id !== currentId) throw new AppError(403, 40301, "Login must be unique");
                }
                if (modelName === 'Story' && body.title) {
                    const dup = await Story.findOne({ where: { title: body.title } });
                    if (dup && dup.id !== currentId) throw new AppError(403, 40302, "Title must be unique");
                }
                if (modelName === 'Label' && body.name) {
                    const dup = await Label.findOne({ where: { name: body.name } });
                    if (dup && dup.id !== currentId) throw new AppError(403, 40303, "Name must be unique");
                }
            }
            next();
        } catch (e) {
            next(e);
        }
    };
}

async function syncLabels(entity, req) {
    const labelsInput = req.body.labels || req.body.labelIds;
    if (labelsInput && Array.isArray(labelsInput)) {
        const oldLabels = await entity.getLabels();
        const oldLabelIds = oldLabels.map(l => l.id);

        const ids = [];
        for (const item of labelsInput) {
            if (typeof item === 'number') {
                ids.push(item);
            } else if (typeof item === 'string') {
                const [l] = await Label.findOrCreate({ where: { name: item } });
                ids.push(l.id);
            } else if (typeof item === 'object') {
                if (item.id) {
                    if (item.name) await Label.update({ name: item.name }, { where: { id: item.id } });
                    ids.push(item.id);
                } else if (item.name) {
                    const [l] = await Label.findOrCreate({ where: { name: item.name } });
                    ids.push(l.id);
                }
            }
        }
        await entity.setLabels(ids);

        const orphanedIds = oldLabelIds.filter(id => !ids.includes(id));
        if (orphanedIds.length > 0) {
            await Label.destroy({ where: { id: orphanedIds } });
        }
    }
}

const mapStory = (story) => {
    const json = story.toJSON();
    json.labels = json.Labels;
    json.labelIds = (json.Labels || []).map(l => l.id);
    delete json.Labels;
    return json;
};

function createCrud(Model) {
    const isStory = Model.name === 'Story';
    return {
        create: async (req, res, next) => {
            try {
                const entity = await Model.create(req.body);
                if (isStory) await syncLabels(entity, req);
                const result = isStory ? mapStory(await Model.findByPk(entity.id, { include: Label })) : entity;
                res.status(201).json(result);
            } catch (e) { next(e); }
        },
        getAll: async (req, res, next) => {
            try {
                const limit = req.query.limit ? parseInt(req.query.limit) : undefined;
                const offset = req.query.offset ? parseInt(req.query.offset) : undefined;
                const order = req.query.sort ? [[req.query.sort, 'ASC']] : undefined;
                const items = await Model.findAll({ limit, offset, order, include: isStory ? Label : undefined });
                res.status(200).json(isStory ? items.map(mapStory) : items);
            } catch (e) { next(e); }
        },
        getById: async (req, res, next) => {
            try {
                const entity = await Model.findByPk(req.params.id, { include: isStory ? Label : undefined });
                if (!entity) throw new AppError(404, 40401, "Entity not found");
                res.status(200).json(isStory ? mapStory(entity) : entity);
            } catch (e) { next(e); }
        },
        update: async (req, res, next) => {
            try {
                const id = req.params.id || req.body.id;
                if (!id) throw new AppError(400, 40000, "ID is required");
                const entity = await Model.findByPk(id);
                if (!entity) throw new AppError(404, 40401, "Entity not found");

                await entity.update(req.body);
                if (isStory) await syncLabels(entity, req);
                const updated = isStory ? mapStory(await Model.findByPk(entity.id, { include: Label })) : await Model.findByPk(entity.id);
                res.status(200).json(updated);
            } catch (e) { next(e); }
        },
        delete: async (req, res, next) => {
            try {
                const id = req.params.id || (req.body ? req.body.id : null) || (req.query ? req.query.id : null);
                if (!id) throw new AppError(400, 40000, "ID is required");

                if (Model.name === 'Story') {
                    const entity = await Model.findByPk(id, { include: Label });
                    if (!entity) throw new AppError(404, 40401, "Entity not found");
                    const labelIds = entity.Labels.map(l => l.id);
                    await Model.destroy({ where: { id } });
                    if (labelIds.length > 0) await Label.destroy({ where: { id: labelIds } });
                    return res.status(204).send();
                }

                if (Model.name === 'Editor') {
                    const stories = await Story.findAll({ where: { editorId: id }, include: Label });
                    for (const story of stories) {
                        const labelIds = story.Labels.map(l => l.id);
                        await story.destroy();
                        if (labelIds.length > 0) await Label.destroy({ where: { id: labelIds } });
                    }
                }

                const deletedCount = await Model.destroy({ where: { id } });
                if (deletedCount === 0) throw new AppError(404, 40401, "Entity not found");
                res.status(204).send();
            } catch (e) { next(e); }
        }
    };
}

const bindRoutes = (path, Model) => {
    const crud = createCrud(Model);
    const validator = getValidator(Model.name);
    router.post(path, validator, crud.create);
    router.get(path, crud.getAll);
    router.get(`${path}/:id`, crud.getById);
    router.put(path, validator, crud.update); 
    router.put(`${path}/:id`, validator, crud.update);
    router.delete(path, crud.delete);
    router.delete(`${path}/:id`, crud.delete);
};

bindRoutes('/editors', Editor);
bindRoutes('/stories', Story);
bindRoutes('/labels', Label);
bindRoutes('/comments', Comment);

router.use((req, res, next) => next(new AppError(404, 40400, `Endpoint not found`)));

module.exports = router;