from cassandra.cluster import Cluster
from cassandra.io.asyncioreactor import AsyncioConnection

cluster = Cluster(
    ["127.0.0.1"],
    port=9042,
    connection_class=AsyncioConnection
)
session = cluster.connect()
print("Connected!")
cluster.shutdown()