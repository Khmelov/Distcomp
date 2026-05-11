from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()


class BaseModel(db.Model):
    """Абстрактная базовая модель"""
    __abstract__ = True

    id = db.Column(db.Integer, primary_key=True, autoincrement=True)