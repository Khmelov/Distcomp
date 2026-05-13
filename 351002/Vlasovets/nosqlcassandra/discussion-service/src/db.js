const cassandra = require('cassandra-driver');

const client = new cassandra.Client({
    contactPoints: ['127.0.0.1'],
    localDataCenter: 'datacenter1' // Стандартно для Cassandra Docker
});

async function initDb() {
    await client.execute("CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};");
    await client.execute("CREATE TABLE IF NOT EXISTS distcomp.tbl_comment (id int PRIMARY KEY, story_id int, content text);");
}

module.exports = { client, initDb };