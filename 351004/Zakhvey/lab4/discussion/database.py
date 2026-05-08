import os
from cassandra.cqlengine import connection
from cassandra.cluster import Cluster
from cassandra.cqlengine.management import sync_table


def init_cassandra():
    host = os.getenv('CASSANDRA_HOST', '127.0.0.1')
    port = int(os.getenv('CASSANDRA_PORT', '9042'))
    cluster = Cluster([host], port=port)
    session = cluster.connect()

    session.execute(
        """
        CREATE KEYSPACE IF NOT EXISTS distcomp 
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
        """
    )

    connection.setup([host], "distcomp", protocol_version=3)
    from . import models  # noqa: E402
    sync_table(models.CommentModel)
    sync_table(models.CommentByIdModel)
