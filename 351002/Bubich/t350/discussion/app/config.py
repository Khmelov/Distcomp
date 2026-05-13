import os


class Config:
    """Конфигурация модуля discussion"""
    CASSANDRA_HOST = os.environ.get('CASSANDRA_HOST', 'localhost')
    CASSANDRA_PORT = int(os.environ.get('CASSANDRA_PORT', 9042))
    CASSANDRA_KEYSPACE = os.environ.get('CASSANDRA_KEYSPACE', 'distcomp')

    SERVER_HOST = 'localhost'
    SERVER_PORT = 24130