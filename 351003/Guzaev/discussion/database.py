from cassandra.cluster import Cluster

cluster = Cluster(['localhost'], port=9042)
session = cluster.connect()

session.execute("""
    CREATE KEYSPACE IF NOT EXISTS distcomp
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
""")
session.set_keyspace('distcomp')

# ВАЖНО: partition key — tweet_id (не country!), иначе будет data skew
# country как partition key — плохо, т.к. все данные одной страны на одной ноде
session.execute("""
    CREATE TABLE IF NOT EXISTS tbl_comment (
        tweet_id bigint,
        country  text,
        id       bigint,
        content  text,
        PRIMARY KEY ((tweet_id), country, id)
    )
""")