# Импорт BaseSettings для автоматического чтения переменных окружения
# Импорт SettingsConfigDict для настройки поведения (префиксы, регистр)
from pydantic_settings import BaseSettings, SettingsConfigDict


# Класс настроек подключения к Cassandra
# Значения читаются из переменных окружения: CASSANDRA_HOST, CASSANDRA_PORT, CASSANDRA_KEYSPACE
class Settings(BaseSettings):
    # Хост Cassandra (по умолчанию localhost, в Docker — "cassandra")
    cassandra_host: str = "localhost"
    # Порт Cassandra (стандартный порт — 9042)
    cassandra_port: int = 9042
    # Имя keyspace (аналог имени базы данных в PostgreSQL)
    cassandra_keyspace: str = "distcomp"
    kafka_bootstrap_servers: str = "localhost:29092"

    # Конфигурация pydantic-settings:
    model_config = SettingsConfigDict(
        env_prefix="",       # Без префикса: переменная CASSANDRA_HOST, а не APP_CASSANDRA_HOST
        case_sensitive=False, # Регистронезависимость: cassandra_host == CASSANDRA_HOST
    )
