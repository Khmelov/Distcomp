import os


class Config:
    CASSANDRA_HOST = os.environ.get('CASSANDRA_HOST', 'localhost')
    CASSANDRA_PORT = int(os.environ.get('CASSANDRA_PORT', 9042))
    CASSANDRA_KEYSPACE = 'distcomp'

    KAFKA_BOOTSTRAP_SERVERS = os.environ.get('KAFKA_BOOTSTRAP_SERVERS', 'localhost:9092')
    KAFKA_IN_TOPIC = 'InTopic'
    KAFKA_OUT_TOPIC = 'OutTopic'