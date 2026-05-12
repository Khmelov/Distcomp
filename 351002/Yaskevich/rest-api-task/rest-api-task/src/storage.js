const { Pool } = require('pg');

const useMemory = process.env.STORAGE_MODE === 'memory';

const pool = useMemory
    ? null
    : new Pool({
          host: process.env.DB_HOST || 'localhost',
          port: Number(process.env.DB_PORT || 5432),
          user: process.env.DB_USER || 'postgres',
          password: process.env.DB_PASSWORD || 'postgres',
          database: process.env.DB_NAME || 'postgres'
      });

async function withTransaction(callback) {
    if (useMemory) {
        return callback(null);
    }

    const client = await pool.connect();
    try {
        await client.query('BEGIN');
        const result = await callback(client);
        await client.query('COMMIT');
        return result;
    } catch (error) {
        await client.query('ROLLBACK');
        throw error;
    } finally {
        client.release();
    }
}

module.exports = {
    pool,
    useMemory,
    withTransaction
};
