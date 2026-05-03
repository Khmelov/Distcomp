# Импорт lru_cache для кэширования результата функции (singleton-паттерн)
from functools import lru_cache

# Импорт BaseSettings для автоматического чтения переменных окружения
# Импорт SettingsConfigDict для настройки поведения класса Settings
from pydantic_settings import BaseSettings, SettingsConfigDict


# Класс настроек подключения к PostgreSQL
# Значения автоматически читаются из переменных окружения
class Settings(BaseSettings):
    # Имя пользователя PostgreSQL (из env: POSTGRES_USER)
    postgres_user: str
    # Пароль PostgreSQL (из env: POSTGRES_PASSWORD)
    postgres_password: str
    # Имя базы данных (из env: POSTGRES_DB)
    postgres_db: str
    # Хост PostgreSQL (из env: POSTGRES_HOST, в Docker — "pg")
    postgres_host: str
    # Порт PostgreSQL (из env: POSTGRES_PORT, обычно 5432)
    postgres_port: int
    kafka_bootstrap_servers: str = "localhost:29092"
    redis_host: str = "localhost"
    redis_port: int = 6379
    jwt_secret: str = "super-secret-key-for-jwt-tokens-rv-project"
    jwt_expire_minutes: int = 30

    # Конфигурация pydantic-settings
    model_config = SettingsConfigDict(
        env_prefix="",        # Без префикса: POSTGRES_USER, а не APP_POSTGRES_USER
        case_sensitive=False   # Регистронезависимость: postgres_user == POSTGRES_USER
    )

# Кэшированная функция получения настроек (вызывается один раз, результат кэшируется)
@lru_cache
def get_settings() -> Settings:
    return Settings()
