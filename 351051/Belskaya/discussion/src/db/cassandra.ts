import cassandra from "cassandra-driver";

export const cassandraClient = new cassandra.Client({
  contactPoints: ["127.0.0.1"],
  localDataCenter: "datacenter1",
});

export async function initCassandra() {
  try {
    await cassandraClient.execute(`
      CREATE KEYSPACE IF NOT EXISTS distcomp
      WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
      };
    `);

    await cassandraClient.execute(`
      CREATE TABLE IF NOT EXISTS distcomp.tbl_message (
        id bigint PRIMARY KEY,
        topic_id bigint,
        country text,
        content text
      );
    `);

    await cassandraClient.execute(`
      CREATE TABLE IF NOT EXISTS distcomp.tbl_message_by_topic (
        topic_id bigint,
        id bigint,
        country text,
        content text,
        PRIMARY KEY ((topic_id), id)
      );
    `);

    console.log("Cassandra initialized successfully.");
  } catch (err) {
    console.error("Failed to initialize Cassandra:", err);
    throw err;
  }
}
