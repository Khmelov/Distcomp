import os


class Config:
    # PostgreSQL/SQLite
    SQLALCHEMY_DATABASE_URI = 'sqlite:///publisher.db'

    # Redis
    REDIS_HOST = os.environ.get('REDIS_HOST', 'localhost')
    REDIS_PORT = int(os.environ.get('REDIS_PORT', 6379))
    REDIS_DB = int(os.environ.get('REDIS_DB', 0))
    REDIS_TTL = int(os.environ.get('REDIS_TTL', 300))  # 5 минут

    # Discussion
    DISCUSSION_SERVICE_URL = 'http://localhost:24130/api/v1.0'

    SQLALCHEMY_TRACK_MODIFICATIONS = False