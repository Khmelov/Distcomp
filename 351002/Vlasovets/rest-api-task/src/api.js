const express = require('express');
const { AppError } = require('./errors');
const router = express.Router();

// бд в оперативе
class InMemoryRepository {
    constructor() {
        this.data = new Map();
        this.currentId = 1;
    }
    save(entity) {
        if (!entity.id) entity.id = this.currentId++;
        this.data.set(Number(entity.id), entity);
        return entity;
    }
    findById(id) { return this.data.get(Number(id)); }
    findAll() { return Array.from(this.data.values()); }
    deleteById(id) { return this.data.delete(Number(id)); }
}

const repos = {
    editors: new InMemoryRepository(),
    stories: new InMemoryRepository(),
    labels: new InMemoryRepository(),
    comments: new InMemoryRepository()
};

function getValidator(repoName) {
    return (req, res, next) => {
        if (['POST', 'PUT'].includes(req.method)) {
            const body = req.body;

            if (!body || Object.keys(body).length === 0) {
                return next(new AppError(400, 40000, "Empty request body"));
            }

            const pathId = req.params.id;
            const bodyId = body.id;

            // Защита от подмены ID
            if (pathId && bodyId !== undefined && String(bodyId) !== String(pathId)) {
                return next(new AppError(400, 40010, "ID in body does not match URL"));
            }

            // Обязательные поля
            const requiredFields = {
                editors:['login', 'password', 'firstname', 'lastname'],
                stories: ['title', 'content', 'editorId'],
                labels: ['name'],
                comments: ['content', 'storyId']
            };

            const reqFields = requiredFields[repoName] || [];
            for (const f of reqFields) {
                if (body[f] === undefined || body[f] === null || body[f] === '') {
                    return next(new AppError(400, 40005, `${f} is required and cannot be empty`));
                }
            }

            // Проверка минимальной длины 
            for (const [key, value] of Object.entries(body)) {
                if (value === null) return next(new AppError(400, 40001, `${key} cannot be null`));
                if (typeof value === 'string' && value.trim().length < 2) {
                    return next(new AppError(400, 40003, `${key} is too short`));
                }
            }

            // Проверка связей с другими сущностями
            if (repoName === 'stories' && body.editorId !== undefined) {
                if (!repos.editors.findById(body.editorId)) {
                    return next(new AppError(400, 40012, "Editor does not exist"));
                }
            }
            if (repoName === 'comments' && body.storyId !== undefined) {
                if (!repos.stories.findById(body.storyId)) {
                    return next(new AppError(400, 40013, "Story does not exist"));
                }
            }

            // Проверка уникальности
            if (repoName === 'editors' && body.login) {
                const currentId = pathId ? Number(pathId) : (bodyId ? Number(bodyId) : null);
                const duplicate = repos.editors.findAll().find(e => e.login === body.login && e.id !== currentId);
                if (duplicate) {
                    return next(new AppError(400, 40011, "Login must be unique"));
                }
            }
        }
        next();
    };
}

function createCrud(repoName) {
    const repo = repos[repoName];
    return {
        create: (req, res, next) => {
            try {
                const saved = repo.save({ ...req.body });
                res.status(201).json(saved);
            } catch (e) { next(e); }
        },
        getAll: (req, res, next) => {
            try { res.status(200).json(repo.findAll()); } catch (e) { next(e); }
        },
        getById: (req, res, next) => {
            try {
                const entity = repo.findById(req.params.id);
                if (!entity) throw new AppError(404, 40401, "Entity not found");
                res.status(200).json(entity);
            } catch (e) { next(e); }
        },
        update: (req, res, next) => {
            try {
              
                const id = req.params.id || req.body.id;
                if (!id) throw new AppError(400, 40000, "ID is required for update");
                
                const existing = repo.findById(id);
                if (!existing) throw new AppError(404, 40401, "Entity not found");
                
                const updated = repo.save({ ...existing, ...req.body, id: Number(id) });
                res.status(200).json(updated);
            } catch (e) { next(e); }
        },
        delete: (req, res, next) => {
            try {
                const deleted = repo.deleteById(req.params.id);
                if (!deleted) throw new AppError(404, 40401, "Entity not found");
                res.status(204).send();
            } catch (e) { next(e); }
        }
    };
}

// связь http методов
const bindRoutes = (path, repoName) => {
    const crud = createCrud(repoName);
    const validator = getValidator(repoName);
    
    router.post(path, validator, crud.create);
    router.get(path, crud.getAll);
    router.get(`${path}/:id`, crud.getById);
    
    router.put(path, validator, crud.update); 
    router.put(`${path}/:id`, validator, crud.update);
    
    router.delete(`${path}/:id`, crud.delete);
};

bindRoutes('/editors', 'editors');
bindRoutes('/stories', 'stories');
bindRoutes('/labels', 'labels');
bindRoutes('/comments', 'comments');


router.use((req, res, next) => {
    next(new AppError(404, 40400, `Endpoint not found: ${req.method} ${req.originalUrl}`));
});

module.exports = router;