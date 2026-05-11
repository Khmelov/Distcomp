import os


class Config:
    DB_USER = os.environ.get('DB_USER', 'postgres')
    DB_PASSWORD = os.environ.get('DB_PASSWORD', 'postgres')
    DB_HOST = os.environ.get('DB_HOST', 'localhost')
    DB_PORT = os.environ.get('DB_PORT', '5432')
    DB_NAME = os.environ.get('DB_NAME', 'distcomp')

    SQLALCHEMY_DATABASE_URI = 'sqlite:///publisher.db'

    # URL для discussion микросервиса
    DISCUSSION_SERVICE_URL = os.environ.get(
        'DISCUSSION_SERVICE_URL',
        'http://localhost:24130/api/v1.0'
    )

    SQLALCHEMY_TRACK_MODIFICATIONS = False
    DEFAULT_PAGE_SIZE = 10
    MAX_PAGE_SIZE = 100