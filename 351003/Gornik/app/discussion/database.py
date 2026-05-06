# Импорт модуля re для регулярных выражений (валидация имени keyspace)
import re

# Импорт класса Cluster из cassandra-driver для подключения к кластеру Cassandra
from cassandra.cluster import Cluster

# Импорт класса Settings для чтения настроек подключения из переменных окружения
from config.settings import Settings

# Создаем экземпляр настроек (читает CASSANDRA_HOST, CASSANDRA_PORT, CASSANDRA_KEYSPACE из env)
settings = Settings()

# Создаем объект кластера Cassandra с указанием хоста и порта
# В Docker хост будет "cassandra" (имя сервиса), локально — "localhost"
cluster = Cluster([settings.cassandra_host], port=settings.cassandra_port)
# Глобальная переменная для хранения сессии Cassandra (паттерн Singleton)
_session = None


# Функция получения (или создания) сессии подключения к Cassandra
def get_session():
    # Используем глобальную переменную _session
    global _session
    # Если сессия еще не создана — создаем новую (ленивая инициализация)
    if _session is None:
        # cluster.connect() устанавливает TCP-соединение с Cassandra и возвращает Session
        _session = cluster.connect()
        # Инициализируем схему БД (keyspace + таблицы) при первом подключении
        _init_schema(_session)
    # Возвращаем существующую или только что созданную сессию
    return _session


# Приватная функция инициализации схемы базы данных (keyspace и таблицы)
def _init_schema(s):
    # Получаем имя keyspace из настроек
    ks = settings.cassandra_keyspace
    # Валидация имени keyspace — допускаются только буквы, цифры и подчеркивание
    # Это защита от CQL-инъекции, т.к. keyspace подставляется через % (не параметризуется)
    if not re.match(r'^[a-zA-Z_][a-zA-Z0-9_]*$', ks):
        # Если имя невалидно — бросаем исключение и приложение не запустится
        raise ValueError(f"Invalid keyspace name: {ks}")

    # Создаем keyspace (аналог CREATE DATABASE в PostgreSQL), если он не существует
    # SimpleStrategy — стратегия репликации для одного дата-центра
    # replication_factor: 1 — данные хранятся на одной ноде (для разработки)
    s.execute("""
        CREATE KEYSPACE IF NOT EXISTS %s
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
    """ % ks)

    # Устанавливаем текущий keyspace (аналог USE database в SQL)
    # После этого все запросы будут выполняться в контексте этого keyspace
    s.set_keyspace(ks)

    # Создаем ОСНОВНУЮ таблицу комментариев (если не существует)
    # tweetId — partition key: определяет, на какой ноде хранятся данные
    #   Все комментарии к одному твиту лежат на одной ноде → быстрый запрос
    # id — clustering key: определяет порядок записей внутри партиции
    # country и content — обычные поля (не являются частью ключа)
    # CLUSTERING ORDER BY (id ASC) — комментарии сортируются по id по возрастанию
    s.execute("""
        CREATE TABLE IF NOT EXISTS tbl_comment (
            tweetId  bigint,
            id       bigint,
            country  text,
            content  text,
            state    text,
            PRIMARY KEY (tweetId, id)
        ) WITH CLUSTERING ORDER BY (id ASC)
    """)

    # Создаем ВСПОМОГАТЕЛЬНУЮ таблицу для поиска комментария по id
    # id — единственный partition key (обеспечивает поиск по id без знания tweetId)
    # Это паттерн денормализации в Cassandra: одни и те же данные в разных таблицах
    # для разных паттернов запросов (query-driven modeling)
    s.execute("""
        CREATE TABLE IF NOT EXISTS tbl_comment_by_id (
            id       bigint PRIMARY KEY,
            tweetId  bigint,
            country  text,
            content  text,
            state    text
        )
    """)

    # Add state column to existing tables if missing (migration for existing data)
    try:
        s.execute("ALTER TABLE tbl_comment ADD state text")
    except Exception:
        pass  # Column already exists
    try:
        s.execute("ALTER TABLE tbl_comment_by_id ADD state text")
    except Exception:
        pass  # Column already exists


# Функция корректного завершения работы — закрывает все соединения с Cassandra
def shutdown():
    # cluster.shutdown() закрывает все сессии и TCP-соединения
    cluster.shutdown()
