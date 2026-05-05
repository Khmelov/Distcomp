const { pool } = require('../db');

async function createCreator(data) {
    const { login, password, firstname, lastname } = data;
    try {
        const res = await pool.query(
            'INSERT INTO tbl_creator (login, password, firstname, lastname) VALUES ($1,$2,$3,$4) RETURNING *',
            [login, password, firstname, lastname]
        );
        return formatCreator(res.rows[0]);
    } catch (e) {
        if (e.code === '23505') return { error: 'duplicate login', code: 40301 };
        throw e;
    }
}

async function getCreator(id) {
    const res = await pool.query('SELECT * FROM tbl_creator WHERE id = $1', [id]);
    if (!res.rows[0]) return null;
    return formatCreator(res.rows[0]);
}

async function getAllCreators() {
    const res = await pool.query('SELECT * FROM tbl_creator ORDER BY id');
    return res.rows.map(formatCreator);
}

async function updateCreator(id, data) {
    const fields = [];
    const values = [];
    let idx = 1;
    for (const [key, val] of Object.entries(data)) {
        if (['login', 'password', 'firstname', 'lastname'].includes(key)) {
            fields.push(`${key} = $${idx++}`);
            values.push(val);
        }
    }
    if (fields.length === 0) return null;
    values.push(id);
    const query = `UPDATE tbl_creator SET ${fields.join(', ')} WHERE id = $${idx} RETURNING *`;
    try {
        const res = await pool.query(query, values);
        return formatCreator(res.rows[0]);
    } catch (e) {
        if (e.code === '23505') return { error: 'duplicate login', code: 40301 };
        throw e;
    }
}

async function deleteCreator(id) {
    const res = await pool.query('DELETE FROM tbl_creator WHERE id = $1', [id]);
    return res.rowCount > 0;
}

function formatCreator(row) {
    return {
        id: Number(row.id),
        login: row.login,
        password: row.password,
        firstname: row.firstname,
        lastname: row.lastname
    };
}

async function createNews(data) {
    const { creatorId, title, content } = data;
    const now = new Date().toISOString();
    const res = await pool.query(
        'INSERT INTO tbl_news (creator_id, title, content, created, modified) VALUES ($1,$2,$3,$4,$5) RETURNING *',
        [creatorId, title, content, now, now]
    );
    return formatNews(res.rows[0]);
}

async function getNews(id) {
    const res = await pool.query('SELECT * FROM tbl_news WHERE id = $1', [id]);
    if (!res.rows[0]) return null;
    return formatNews(res.rows[0]);
}

async function getAllNews() {
    const res = await pool.query('SELECT * FROM tbl_news ORDER BY id');
    return res.rows.map(formatNews);
}

async function updateNews(id, data) {
    const fields = [];
    const values = [];
    let idx = 1;
    if (data.title !== undefined)   { fields.push(`title = $${idx++}`); values.push(data.title); }
    if (data.content !== undefined) { fields.push(`content = $${idx++}`); values.push(data.content); }
    if (data.creatorId !== undefined) {
        fields.push(`creator_id = $${idx++}`);
        values.push(data.creatorId);
    }
    if (fields.length === 0) return null;
    fields.push(`modified = NOW()`);
    values.push(id);
    const res = await pool.query(
        `UPDATE tbl_news SET ${fields.join(', ')} WHERE id = $${idx} RETURNING *`,
        values
    );
    return formatNews(res.rows[0]);
}

async function deleteNews(id) {
    const res = await pool.query('DELETE FROM tbl_news WHERE id = $1', [id]);
    return res.rowCount > 0;
}

function formatNews(row) {
    return {
        id: Number(row.id),
        creatorId: Number(row.creator_id),
        title: row.title,
        content: row.content,
        created: row.created,
        modified: row.modified
    };
}

async function createSticker(data) {
    const { name } = data;
    const res = await pool.query('INSERT INTO tbl_sticker (name) VALUES ($1) RETURNING *', [name]);
    return formatSticker(res.rows[0]);
}

async function getSticker(id) {
    const res = await pool.query('SELECT * FROM tbl_sticker WHERE id = $1', [id]);
    if (!res.rows[0]) return null;
    return formatSticker(res.rows[0]);
}

async function getAllStickers() {
    const res = await pool.query('SELECT * FROM tbl_sticker ORDER BY id');
    return res.rows.map(formatSticker);
}

async function updateSticker(id, data) {
    const fields = [];
    const values = [];
    let idx = 1;
    if (data.name !== undefined) { fields.push(`name = $${idx++}`); values.push(data.name); }
    if (fields.length === 0) return null;
    values.push(id);
    const res = await pool.query(
        `UPDATE tbl_sticker SET ${fields.join(', ')} WHERE id = $${idx} RETURNING *`,
        values
    );
    return formatSticker(res.rows[0]);
}

async function deleteSticker(id) {
    const res = await pool.query('DELETE FROM tbl_sticker WHERE id = $1', [id]);
    return res.rowCount > 0;
}

function formatSticker(row) {
    return {
        id: Number(row.id),
        name: row.name
    };
}

module.exports = {
    createCreator, getCreator, getAllCreators, updateCreator, deleteCreator,
    createNews, getNews, getAllNews, updateNews, deleteNews,
    createSticker, getSticker, getAllStickers, updateSticker, deleteSticker
};