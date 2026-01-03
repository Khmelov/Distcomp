CREATE KEYSPACE IF NOT EXISTS distcomp 
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};