class Config:
    """Конфигурация для PostgreSQL"""
    DB_USER = 'postgres'
    DB_PASSWORD = 'postgres'
    DB_HOST = 'localhost'
    DB_PORT = '5432'
    DB_NAME = 'distcomp'

    SQLALCHEMY_DATABASE_URI = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

    # Для SQLite (если не используете PostgreSQL)
    # SQLALCHEMY_DATABASE_URI = 'sqlite:///distcomp.db'

    SQLALCHEMY_TRACK_MODIFICATIONS = False