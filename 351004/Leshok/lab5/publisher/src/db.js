const { Pool } = require('pg');

const pool = new Pool({
    user: 'postgres',
    password: 'postgres',
    host: 'localhost',
    database: 'distcomp',
    port: 5432
});

async function init() {
    const client = await pool.connect();
    try {
        await client.query(`
            CREATE TABLE IF NOT EXISTS tbl_creator (
                id SERIAL PRIMARY KEY,
                login VARCHAR(64) UNIQUE NOT NULL,
                password VARCHAR(128) NOT NULL,
                firstname VARCHAR(64) NOT NULL,
                lastname VARCHAR(64) NOT NULL
            )
        `);
        await client.query(`
            CREATE TABLE IF NOT EXISTS tbl_news (
                id SERIAL PRIMARY KEY,
                creator_id INTEGER REFERENCES tbl_creator(id),
                title VARCHAR(64) NOT NULL,
                content VARCHAR(2048) NOT NULL,
                created TIMESTAMP DEFAULT NOW(),
                modified TIMESTAMP DEFAULT NOW()
            )
        `);
        await client.query(`
            CREATE TABLE IF NOT EXISTS tbl_sticker (
                id SERIAL PRIMARY KEY,
                name VARCHAR(32) NOT NULL
            )
        `);
        console.log('PostgreSQL tables ready');
    } finally {
        client.release();
    }
}

module.exports = { pool, init };