from cassandra.cluster import Cluster
from cassandra.cqlengine import connection


def connect_to_cassandra():
    # Ждем пока Cassandra станет доступна
    from time import sleep
    from cassandra.cluster import Cluster
    from cassandra.auth import PlainTextAuthProvider

    for _ in range(10):  # 10 попыток
        try:
            cluster = Cluster(
                ['cassandra'],
                port=9042,
                auth_provider=PlainTextAuthProvider(username='cassandra', password='cassandra')
            )
            session = cluster.connect()

            session.execute("""
                CREATE KEYSPACE IF NOT EXISTS nosql_db 
                WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 }
            """)

            connection.setup(['cassandra'], "nosql_db")
            return session
        except Exception as e:
            print(f"Ошибка подключения: {e}. Повторная попытка...")
            sleep(5)

    raise RuntimeError("Не удалось подключиться к Cassandra после 10 попыток")