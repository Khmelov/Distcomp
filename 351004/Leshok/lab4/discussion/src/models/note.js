const cassandra = require('cassandra-driver');
const { Integer } = cassandra.types;

const client = new cassandra.Client({
    contactPoints: ['localhost'],
    localDataCenter: 'datacenter1'
});

let nextId = 1; 

async function waitForCassandra() {
    let connected = false;
    for (let i = 0; i < 30; i++) {
        try {
            await client.connect();
            connected = true;
            break;
        } catch (e) {
            console.log('Waiting for Cassandra...');
            await new Promise(res => setTimeout(res, 2000));
        }
    }
    if (!connected) throw new Error('Could not connect to Cassandra');
}

async function init() {
    await waitForCassandra();
    await client.execute(`
        CREATE KEYSPACE IF NOT EXISTS distcomp
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
    `);
    await client.execute(`
        CREATE TABLE IF NOT EXISTS distcomp.tbl_note (
            id INT PRIMARY KEY,
            news_id INT,
            content TEXT,
            state TEXT
        )
    `);
    const result = await client.execute('SELECT MAX(id) as max_id FROM distcomp.tbl_note');
    if (result.rowLength > 0 && result.first().max_id !== null) {
        nextId = result.first().max_id + 1;
    }
    console.log('Cassandra schema ready, nextId =', nextId);
}

async function getNote(id) {
    const result = await client.execute(
        'SELECT id, news_id, content, state FROM distcomp.tbl_note WHERE id = ?',
        [id],
        { prepare: true }
    );
    if (result.rowLength === 0) return null;
    const row = result.first();
    return {
        id: row.id,
        newsId: row.news_id,
        content: row.content,
        state: row.state
    };
}

async function createNote({ content, newsId }) {
    const id = nextId++;
    const state = 'PENDING';
    const newsIdInt = parseInt(newsId, 10);
    if (isNaN(newsIdInt)) throw new Error('newsId must be a valid integer');

    await client.execute(
        'INSERT INTO distcomp.tbl_note (id, news_id, content, state) VALUES (?, ?, ?, ?)',
        [id, Integer.fromNumber(newsIdInt), content, state],
        { prepare: true }
    );
    return { id, newsId: newsIdInt, content, state };
}

async function updateNote(id, fields) {
    const sets = [];
    const params = [];
    if (fields.content !== undefined) { sets.push('content = ?'); params.push(fields.content); }
    if (fields.state !== undefined)   { sets.push('state = ?');   params.push(fields.state); }
    if (fields.newsId !== undefined) {
        const newsIdInt = parseInt(fields.newsId, 10);
        if (isNaN(newsIdInt)) throw new Error('newsId must be a valid integer');
        sets.push('news_id = ?');
        params.push(Integer.fromNumber(newsIdInt));
    }
    if (sets.length === 0) return;
    params.push(id);
    await client.execute(`UPDATE distcomp.tbl_note SET ${sets.join(', ')} WHERE id = ?`, params, { prepare: true });
}

async function deleteNote(id) {
    await client.execute('DELETE FROM distcomp.tbl_note WHERE id = ?', [id], { prepare: true });
}

async function getAllNotes() {
    const result = await client.execute('SELECT id, news_id, content, state FROM distcomp.tbl_note');
    return result.rows.map(row => ({
        id: row.id,
        newsId: row.news_id,
        content: row.content,
        state: row.state
    }));
}

module.exports = { init, getNote, createNote, updateNote, deleteNote, getAllNotes };