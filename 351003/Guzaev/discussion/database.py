from cassandra.cluster import Cluster

cluster = Cluster(['localhost'], port=9042)
session = cluster.connect()

session.execute("""
    CREATE KEYSPACE IF NOT EXISTS distcomp
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
""")
session.set_keyspace('distcomp')
session.execute("""
    CREATE TABLE IF NOT EXISTS tbl_comment (
        id int,
        tweet_id int,
        content text,
        country text,
        state text,
        PRIMARY KEY (tweet_id, id)
    )
""")