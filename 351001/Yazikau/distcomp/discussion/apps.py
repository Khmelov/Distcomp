from django.apps import AppConfig
import os


class DiscussionConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'discussion'

    def ready(self):
        # Этот код выполнится при старте сервера
        import sys
        # Не выполняем это при миграциях, чтобы не было конфликтов
        if 'runserver' not in sys.argv:
            return

        from cassandra.cluster import Cluster
        from cassandra.cqlengine import connection
        from cassandra.cqlengine.management import sync_table

        # Берем хост из docker-compose (или используем localhost для тестов без докера)
        cassandra_host = os.environ.get('CASSANDRA_HOST', 'cassandra')

        try:
            # 1. Создаем Keyspace (базу данных), если её еще нет
            cluster = Cluster([cassandra_host], port=9042)
            session = cluster.connect()
            session.execute("""
                CREATE KEYSPACE IF NOT EXISTS distcomp
                WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
                """)
            cluster.shutdown()

            # 2. Настраиваем ORM подключение для твоих моделей
            connection.setup([cassandra_host], "distcomp", protocol_version=3)

            # 3. Автоматически создаем таблицу для комментариев
            # Импортируем модель прямо здесь, чтобы избежать циклических импортов
            from .models import CassandraComment
            sync_table(CassandraComment)

            print("✅ Успешно подключились к Cassandra и синхронизировали таблицы!")

        except Exception as e:
            print(f"❌ Ошибка подключения к Cassandra: {e}")