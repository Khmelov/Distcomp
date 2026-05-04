from cassandra.cqlengine import connection
from cassandra.cluster import Cluster
from cassandra.cqlengine.management import sync_table
import os


def init_cassandra():
    # Подключаемся к Cassandra из окружения (для Docker) или локально
    host = os.getenv('CASSANDRA_HOST', '127.0.0.1')
    port = int(os.getenv('CASSANDRA_PORT', '9042'))
    cluster = Cluster([host], port=port)
    session = cluster.connect()

    # Создаем пространство имен (Keyspace)
    session.execute("""
        CREATE KEYSPACE IF NOT EXISTS distcomp 
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
    """)

    # Настраиваем соединение для моделей
    connection.setup([host], "distcomp", protocol_version=3)
    from .models import CommentModel, CommentByIdModel
    sync_table(CommentModel)
    sync_table(CommentByIdModel)
