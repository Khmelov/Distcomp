const cassandra = require("cassandra-driver");

const client = new cassandra.Client({
  contactPoints: ["localhost"],
  localDataCenter: "datacenter1",
  keyspace: "discussion",
});

client
  .connect()
  .then(() => console.log("Cassandra connected"))
  .catch((err) => console.error(err));

module.exports = client;
