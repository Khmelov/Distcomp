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
        content text,
        state text
      );
    `);

    await cassandraClient.execute(`
      CREATE TABLE IF NOT EXISTS distcomp.tbl_message_by_topic (
        topic_id bigint,
        bucket int,
        id bigint,
        country text,
        content text,
        state text,
        PRIMARY KEY ((topic_id, bucket), id)
      ) WITH CLUSTERING ORDER BY (id ASC);
    `);

    console.log("Cassandra initialized successfully.");
  } catch (err) {
    console.error("Failed to initialize Cassandra:", err);
    throw err;
  }
}
