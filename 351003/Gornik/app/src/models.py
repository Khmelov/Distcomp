# Импорт datetime для полей created_at и modified_at в модели Tweet
from datetime import datetime

# Импорт компонентов SQLAlchemy для определения колонок и связей
from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey, Table
# Импорт relationship для создания связей между таблицами (ORM)
from sqlalchemy.orm import relationship

# Импорт базового класса Base, от которого наследуются все модели
from database import Base

# === Промежуточная таблица для связи многие-ко-многим между Tweet и Sticker ===
# В реляционных БД связь M:N реализуется через промежуточную (junction) таблицу
tweet_sticker = Table(
    "tweet_sticker",          # Имя таблицы в PostgreSQL
    Base.metadata,            # Привязка к метаданным SQLAlchemy (для auto-create)
    # Внешний ключ на tbl_tweet.id — часть составного первичного ключа
    Column("tweet_id", Integer, ForeignKey("tbl_tweet.id"), primary_key=True),
    # Внешний ключ на tbl_sticker.id — часть составного первичного ключа
    Column("sticker_id", Integer, ForeignKey("tbl_sticker.id"), primary_key=True),
)

# === Модель Writer (автор) — таблица tbl_writer ===
class Writer(Base):
    __tablename__ = "tbl_writer"  # Имя таблицы в PostgreSQL (с обязательным префиксом tbl_)
    # Первичный ключ с автоинкрементом (PostgreSQL SERIAL)
    id = Column(Integer, primary_key=True, autoincrement=True, nullable=False)
    # Логин автора — уникальный, от 2 до 64 символов
    login = Column(String(64), unique=True, nullable=False)
    # Пароль автора — от 8 до 128 символов (BCrypt hash для v2.0)
    password = Column(String(256), nullable=False)
    # Имя автора — от 2 до 64 символов
    firstname = Column(String(64), nullable=False)
    # Фамилия автора — от 2 до 64 символов
    lastname = Column(String(64), nullable=False)
    # Роль пользователя: ADMIN или CUSTOMER
    role = Column(String(16), nullable=True, default="CUSTOMER")

    # ORM-связь: один Writer → много Tweet (обратная сторона — Tweet.writer)
    tweets = relationship("Tweet", back_populates="writer")

# === Модель Tweet (твит) — таблица tbl_tweet ===
class Tweet(Base):
    __tablename__ = "tbl_tweet"  # Имя таблицы в PostgreSQL
    # Первичный ключ с автоинкрементом
    id = Column(Integer, primary_key=True, autoincrement=True, nullable=False)
    # Заголовок твита — уникальный, от 2 до 64 символов
    title = Column(String(64), unique=True, nullable=False)
    # Содержимое твита — от 4 до 2048 символов
    content = Column(String(2048), nullable=False)
    # Дата создания — устанавливается автоматически при создании
    created_at = Column(DateTime, default=datetime.utcnow)
    # Дата изменения — устанавливается автоматически при создании
    modified_at = Column(DateTime, default=datetime.utcnow)

    # Внешний ключ на tbl_writer.id — связь "какой автор написал этот твит"
    # "writer_id" — имя колонки в БД, writerId — имя атрибута в Python (camelCase для API)
    writerId = Column("writer_id", Integer, ForeignKey('tbl_writer.id'), nullable=False)
    # ORM-связь: Tweet → Writer (обратная сторона — Writer.tweets)
    writer = relationship("Writer", back_populates="tweets")

    # ORM-связь M:N: Tweet ↔ Sticker через промежуточную таблицу tweet_sticker
    stickers = relationship("Sticker", secondary=tweet_sticker, back_populates="tweets")

    # ПРИМЕЧАНИЕ: связь Tweet.comments была УДАЛЕНА при модуляризации
    # Комментарии теперь хранятся в Cassandra через микросервис discussion


# === Модель Sticker (стикер) — таблица tbl_sticker ===
class Sticker(Base):
    __tablename__ = "tbl_sticker"  # Имя таблицы в PostgreSQL
    # Первичный ключ с автоинкрементом
    id = Column(Integer, primary_key=True, autoincrement=True, nullable=False)
    # Название стикера — от 2 до 32 символов
    name = Column(String(32), nullable=False)

    # ORM-связь M:N: Sticker ↔ Tweet через промежуточную таблицу tweet_sticker
    tweets = relationship("Tweet", secondary=tweet_sticker, back_populates="stickers")
