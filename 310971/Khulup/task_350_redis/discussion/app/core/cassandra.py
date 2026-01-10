from cassandra.cluster import Cluster
from cassandra.query import SimpleStatement
import logging

class CassandraConfig:
    def __init__(self):
        self.cluster = None
        self.session = None
    
    def connect(self):
        try:
            self.cluster = Cluster(['localhost'], port=9042)
            self.session = self.cluster.connect()
            
            self.session.execute("""
                CREATE KEYSPACE IF NOT EXISTS distcomp 
                WITH REPLICATION = { 
                    'class' : 'SimpleStrategy', 
                    'replication_factor' : 1 
                }
            """)
            
            self.session.set_keyspace('distcomp')
            
            logging.info("Connected to Cassandra successfully")
            return True
            
        except Exception as e:
            logging.error(f"Failed to connect to Cassandra: {e}")
            return False
    
    def disconnect(self):
        try:
            if self.cluster:
                self.cluster.shutdown()
                logging.info("Disconnected from Cassandra")
        except Exception as e:
            logging.error(f"Error disconnecting from Cassandra: {e}")
    
    def get_session(self):
        return self.session

cassandra_config = CassandraConfig()
