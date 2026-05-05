const cassandra = require('cassandra-driver');

const client = new cassandra.Client({
  contactPoints: [process.env.CASSANDRA_HOST || 'localhost'],
  port: process.env.CASSANDRA_PORT || 9042,
  keyspace: process.env.CASSANDRA_KEYSPACE || 'distcomp',
  localDataCenter: 'datacenter1'
});

async function connect() {
  await client.connect();
  console.log('Connected to Cassandra');
}

module.exports = { client, connect };