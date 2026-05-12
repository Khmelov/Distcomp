const express = require('express');
const { AppError } = require('./errors');
const { pool, useMemory, withTransaction } = require('./storage');

const router = express.Router();

const MEMORY_REPOS = {
    creators: [],
    topics: [],
    marks: [],
    posts: []
};
const MEMORY_IDS = { creators: 1, topics: 1, marks: 1, posts: 1 };
const topicMarkLinks = new Map();

const ENTITIES = {
    creators: {
        table: 'tbl_creator',
        searchFields: ['login', 'firstname', 'lastname'],
        fields: ['login', 'password', 'firstname', 'lastname'],
        required: ['login', 'password', 'firstname', 'lastname']
    },
    topics: {
        table: 'tbl_topic',
        searchFields: ['title'],
        fields: ['title', 'creator_id'],
        required: ['title', 'creatorId']
    },
    marks: {
        table: 'tbl_mark',
        searchFields: ['name'],
        fields: ['name'],
        required: ['name']
    },
    posts: {
        table: 'tbl_post',
        searchFields: ['title', 'content'],
        fields: ['title', 'content', 'topic_id'],
        required: ['title', 'content', 'topicId']
    }
};

function toDbPayload(entityName, body) {
    if (entityName === 'topics') {
        return { title: body.title, creator_id: Number(body.creatorId) };
    }
    if (entityName === 'posts') {
        return { title: body.title, content: body.content, topic_id: Number(body.topicId) };
    }
    if (entityName === 'marks') return { name: body.name };
    return {
        login: body.login,
        password: body.password,
        firstname: body.firstname,
        lastname: body.lastname
    };
}

function fromDb(entityName, row, markIds = undefined) {
    if (!row) return null;
    if (entityName === 'topics') {
        return { id: row.id, title: row.title, creatorId: row.creator_id, markIds: markIds || [] };
    }
    if (entityName === 'posts') {
        return { id: row.id, title: row.title, content: row.content, topicId: row.topic_id };
    }
    return row;
}

function parseListParams(req, entity) {
    const page = Math.max(1, Number(req.query.page || 1));
    const size = Math.min(100, Math.max(1, Number(req.query.size || 20)));
    const sortOrder = String(req.query.sortOrder || 'asc').toLowerCase() === 'desc' ? 'DESC' : 'ASC';
    const defaultSort = entity.fields.includes('title') ? 'title' : 'id';
    const sortByRaw = req.query.sortBy || defaultSort;
    const allowedSort = ['id', ...entity.fields];
    if (!allowedSort.includes(sortByRaw)) {
        throw new AppError(400, 40006, `Unsupported sortBy: ${sortByRaw}`);
    }

    const q = req.query.q ? String(req.query.q).trim() : '';
    const filters = {};
    Object.entries(req.query).forEach(([key, value]) => {
        if (['page', 'size', 'sortBy', 'sortOrder', 'q'].includes(key)) return;
        if (value === undefined || value === '') return;
        filters[key] = value;
    });

    return { page, size, sortBy: sortByRaw, sortOrder, q, filters };
}

function validateRequest(entityName, body, pathId) {
    if (!body || Object.keys(body).length === 0) {
        throw new AppError(400, 40000, 'Empty request body');
    }
    if (pathId && body.id !== undefined && Number(pathId) !== Number(body.id)) {
        throw new AppError(400, 40010, 'ID in body does not match URL');
    }

    const entity = ENTITIES[entityName];
    entity.required.forEach((field) => {
        if (body[field] === undefined || body[field] === null || body[field] === '') {
            throw new AppError(400, 40005, `${field} is required`);
        }
    });
}

async function ensureForeignKeys(entityName, body) {
    if (useMemory) {
        if (entityName === 'topics' && !MEMORY_REPOS.creators.find((x) => x.id === Number(body.creatorId))) {
            throw new AppError(400, 40012, 'Creator does not exist');
        }
        if (entityName === 'posts' && !MEMORY_REPOS.topics.find((x) => x.id === Number(body.topicId))) {
            throw new AppError(400, 40013, 'Topic does not exist');
        }
        if (entityName === 'topics' && Array.isArray(body.markIds)) {
            for (const markId of body.markIds) {
                if (!MEMORY_REPOS.marks.find((x) => x.id === Number(markId))) {
                    throw new AppError(400, 40014, `Mark does not exist: ${markId}`);
                }
            }
        }
        return;
    }

    if (entityName === 'topics') {
        const creator = await pool.query('SELECT id FROM distcomp.tbl_creator WHERE id = $1', [Number(body.creatorId)]);
        if (!creator.rows[0]) throw new AppError(400, 40012, 'Creator does not exist');
    }
    if (entityName === 'posts') {
        const topic = await pool.query('SELECT id FROM distcomp.tbl_topic WHERE id = $1', [Number(body.topicId)]);
        if (!topic.rows[0]) throw new AppError(400, 40013, 'Topic does not exist');
    }
    if (entityName === 'topics' && Array.isArray(body.markIds)) {
        for (const markId of body.markIds) {
            const mark = await pool.query('SELECT id FROM distcomp.tbl_mark WHERE id = $1', [Number(markId)]);
            if (!mark.rows[0]) throw new AppError(400, 40014, `Mark does not exist: ${markId}`);
        }
    }
}

async function createEntity(entityName, body) {
    await ensureForeignKeys(entityName, body);
    if (useMemory) {
        const payload = entityName === 'topics' ? { title: body.title, creatorId: Number(body.creatorId) } : entityName === 'posts' ? { title: body.title, content: body.content, topicId: Number(body.topicId) } : entityName === 'marks' ? { name: body.name } : { login: body.login, password: body.password, firstname: body.firstname, lastname: body.lastname };
        const item = { ...payload, id: MEMORY_IDS[entityName]++ };
        MEMORY_REPOS[entityName].push(item);
        if (entityName === 'topics') topicMarkLinks.set(item.id, (body.markIds || []).map(Number));
        return entityName === 'topics' ? { ...item, markIds: topicMarkLinks.get(item.id) || [] } : item;
    }

    return withTransaction(async (client) => {
        const entity = ENTITIES[entityName];
        const payload = toDbPayload(entityName, body);
        const cols = Object.keys(payload);
        const vals = Object.values(payload);
        const placeholders = cols.map((_, i) => `$${i + 1}`).join(', ');
        const created = await client.query(
            `INSERT INTO distcomp.${entity.table} (${cols.join(', ')}) VALUES (${placeholders}) RETURNING *`,
            vals
        );
        const row = created.rows[0];

        if (entityName === 'topics') {
            const markIds = (body.markIds || []).map(Number);
            for (const markId of markIds) {
                await client.query(
                    'INSERT INTO distcomp.tbl_topic_mark (topic_id, mark_id) VALUES ($1, $2)',
                    [row.id, markId]
                );
            }
            return fromDb(entityName, row, markIds);
        }
        return fromDb(entityName, row);
    });
}

async function getEntityById(entityName, id) {
    if (useMemory) {
        const row = MEMORY_REPOS[entityName].find((x) => x.id === Number(id));
        if (!row) return null;
        if (entityName === 'topics') return { ...row, markIds: topicMarkLinks.get(row.id) || [] };
        return row;
    }

    const entity = ENTITIES[entityName];
    const result = await pool.query(`SELECT * FROM distcomp.${entity.table} WHERE id = $1`, [Number(id)]);
    if (!result.rows[0]) return null;
    if (entityName !== 'topics') return fromDb(entityName, result.rows[0]);
    const links = await pool.query('SELECT mark_id FROM distcomp.tbl_topic_mark WHERE topic_id = $1 ORDER BY mark_id ASC', [
        Number(id)
    ]);
    return fromDb(entityName, result.rows[0], links.rows.map((x) => x.mark_id));
}

async function listEntities(entityName, query) {
    const entity = ENTITIES[entityName];
    const { page, size, sortBy, sortOrder, q, filters } = parseListParams({ query }, entity);

    if (useMemory) {
        let items = [...MEMORY_REPOS[entityName]];
        Object.entries(filters).forEach(([k, v]) => {
            items = items.filter((x) => String(x[k]) === String(v));
        });
        if (q) {
            const ql = q.toLowerCase();
            items = items.filter((row) => entity.searchFields.some((f) => String(row[f] || '').toLowerCase().includes(ql)));
        }
        items.sort((a, b) => {
            const av = a[sortBy];
            const bv = b[sortBy];
            if (av === bv) return 0;
            return (av > bv ? 1 : -1) * (sortOrder === 'DESC' ? -1 : 1);
        });
        const total = items.length;
        const paged = items.slice((page - 1) * size, page * size).map((x) => (entityName === 'topics' ? { ...x, markIds: topicMarkLinks.get(x.id) || [] } : x));
        return { page, size, total, items: paged };
    }

    const whereParts = [];
    const values = [];
    let index = 1;

    Object.entries(filters).forEach(([key, value]) => {
        if (!['id', ...entity.fields].includes(key)) return;
        whereParts.push(`${key} = $${index++}`);
        values.push(value);
    });

    if (q) {
        const ors = entity.searchFields.map((field) => `${field} ILIKE $${index}`).join(' OR ');
        whereParts.push(`(${ors})`);
        values.push(`%${q}%`);
        index += 1;
    }

    const whereSql = whereParts.length ? `WHERE ${whereParts.join(' AND ')}` : '';
    const countResult = await pool.query(`SELECT COUNT(*)::int AS total FROM distcomp.${entity.table} ${whereSql}`, values);
    const total = countResult.rows[0].total;

    values.push(size);
    values.push((page - 1) * size);
    const rowsResult = await pool.query(
        `SELECT * FROM distcomp.${entity.table} ${whereSql} ORDER BY ${sortBy} ${sortOrder} LIMIT $${index++} OFFSET $${index}`,
        values
    );

    let rows = rowsResult.rows.map((r) => fromDb(entityName, r));
    if (entityName === 'topics' && rows.length > 0) {
        const ids = rows.map((x) => x.id);
        const links = await pool.query(
            'SELECT topic_id, mark_id FROM distcomp.tbl_topic_mark WHERE topic_id = ANY($1::int[]) ORDER BY mark_id ASC',
            [ids]
        );
        const grouped = new Map();
        links.rows.forEach((row) => {
            if (!grouped.has(row.topic_id)) grouped.set(row.topic_id, []);
            grouped.get(row.topic_id).push(row.mark_id);
        });
        rows = rows.map((topic) => ({ ...topic, markIds: grouped.get(topic.id) || [] }));
    }
    return { page, size, total, items: rows };
}

async function updateEntity(entityName, id, body) {
    await ensureForeignKeys(entityName, body);
    if (useMemory) {
        const idx = MEMORY_REPOS[entityName].findIndex((x) => x.id === Number(id));
        if (idx < 0) return null;
        const payload = entityName === 'topics' ? { title: body.title, creatorId: Number(body.creatorId) } : entityName === 'posts' ? { title: body.title, content: body.content, topicId: Number(body.topicId) } : entityName === 'marks' ? { name: body.name } : { login: body.login, password: body.password, firstname: body.firstname, lastname: body.lastname };
        MEMORY_REPOS[entityName][idx] = { ...MEMORY_REPOS[entityName][idx], ...payload };
        if (entityName === 'topics') {
            topicMarkLinks.set(Number(id), (body.markIds || []).map(Number));
            return { ...MEMORY_REPOS[entityName][idx], markIds: topicMarkLinks.get(Number(id)) || [] };
        }
        return MEMORY_REPOS[entityName][idx];
    }

    return withTransaction(async (client) => {
        const entity = ENTITIES[entityName];
        const existing = await client.query(`SELECT * FROM distcomp.${entity.table} WHERE id = $1`, [Number(id)]);
        if (!existing.rows[0]) return null;
        const payload = toDbPayload(entityName, body);
        const cols = Object.keys(payload);
        const vals = Object.values(payload);
        const setSql = cols.map((c, i) => `${c} = $${i + 1}`).join(', ');
        vals.push(Number(id));
        const updated = await client.query(
            `UPDATE distcomp.${entity.table} SET ${setSql} WHERE id = $${cols.length + 1} RETURNING *`,
            vals
        );
        const row = updated.rows[0];
        if (entityName === 'topics') {
            await client.query('DELETE FROM distcomp.tbl_topic_mark WHERE topic_id = $1', [Number(id)]);
            const markIds = (body.markIds || []).map(Number);
            for (const markId of markIds) {
                await client.query(
                    'INSERT INTO distcomp.tbl_topic_mark (topic_id, mark_id) VALUES ($1, $2)',
                    [Number(id), markId]
                );
            }
            return fromDb(entityName, row, markIds);
        }
        return fromDb(entityName, row);
    });
}

async function deleteEntity(entityName, id) {
    if (useMemory) {
        const idx = MEMORY_REPOS[entityName].findIndex((x) => x.id === Number(id));
        if (idx < 0) return false;
        MEMORY_REPOS[entityName].splice(idx, 1);
        if (entityName === 'topics') topicMarkLinks.delete(Number(id));
        return true;
    }
    const entity = ENTITIES[entityName];
    const deleted = await pool.query(`DELETE FROM distcomp.${entity.table} WHERE id = $1 RETURNING id`, [Number(id)]);
    return Boolean(deleted.rows[0]);
}

function bindRoutes(path, entityName) {
    router.post(path, async (req, res, next) => {
        try {
            validateRequest(entityName, req.body);
            const created = await createEntity(entityName, req.body);
            res.status(201).json(created);
        } catch (e) {
            next(e);
        }
    });

    router.get(path, async (req, res, next) => {
        try {
            const result = await listEntities(entityName, req.query);
            res.status(200).json(result);
        } catch (e) {
            next(e);
        }
    });

    router.get(`${path}/:id`, async (req, res, next) => {
        try {
            const entity = await getEntityById(entityName, req.params.id);
            if (!entity) throw new AppError(404, 40401, 'Entity not found');
            res.status(200).json(entity);
        } catch (e) {
            next(e);
        }
    });

    router.put(`${path}/:id`, async (req, res, next) => {
        try {
            validateRequest(entityName, req.body, req.params.id);
            const updated = await updateEntity(entityName, req.params.id, req.body);
            if (!updated) throw new AppError(404, 40401, 'Entity not found');
            res.status(200).json(updated);
        } catch (e) {
            next(e);
        }
    });

    router.delete(`${path}/:id`, async (req, res, next) => {
        try {
            const ok = await deleteEntity(entityName, req.params.id);
            if (!ok) throw new AppError(404, 40401, 'Entity not found');
            res.status(204).send();
        } catch (e) {
            next(e);
        }
    });
}

bindRoutes('/creators', 'creators');
bindRoutes('/topics', 'topics');
bindRoutes('/marks', 'marks');
bindRoutes('/posts', 'posts');

router.use((req, res, next) => {
    next(new AppError(404, 40400, `Endpoint not found: ${req.method} ${req.originalUrl}`));
});

module.exports = router;