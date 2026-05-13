import os


class Config:
    # Database
    SQLALCHEMY_DATABASE_URI = 'sqlite:///publisher.db'
    SQLALCHEMY_TRACK_MODIFICATIONS = False

    # JWT
    JWT_SECRET_KEY = os.environ.get('JWT_SECRET_KEY', 'super-secret-key-change-in-production')
    JWT_EXPIRATION_HOURS = int(os.environ.get('JWT_EXPIRATION_HOURS', 24))

    # Redis
    REDIS_HOST = os.environ.get('REDIS_HOST', 'localhost')
    REDIS_PORT = int(os.environ.get('REDIS_PORT', 6379))
    REDIS_DB = int(os.environ.get('REDIS_DB', 0))
    REDIS_TTL = int(os.environ.get('REDIS_TTL', 300))

    # Discussion
    DISCUSSION_SERVICE_URL = 'http://localhost:24130/api/v1.0'