from datetime import datetime

from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey, Table
from sqlalchemy.orm import relationship

from database import Base

tweet_sticker = Table(
    "tweet_sticker",
    Base.metadata,
    Column("tweet_id", Integer, ForeignKey("tbl_tweet.id"), primary_key=True),
    Column("sticker_id", Integer, ForeignKey("tbl_sticker.id"), primary_key=True),
)

class Writer(Base):
    __tablename__ = "tbl_writer"
    id = Column(Integer, primary_key=True, autoincrement=True, nullable=False)
    login = Column(String(64), unique=True, nullable=False)
    password = Column(String(128), nullable=False)
    firstname = Column(String(64), nullable=False)
    lastname = Column(String(64), nullable=False)

    tweets = relationship("Tweet", back_populates="writer")

class Tweet(Base):
    __tablename__ = "tbl_tweet"
    id = Column(Integer, primary_key=True, autoincrement=True, nullable=False)
    title = Column(String(64), unique=True, nullable=False)
    content = Column(String(2048), nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    modified_at = Column(DateTime, default=datetime.utcnow)

    # Исправлено здесь:
    writerId = Column("writer_id", Integer, ForeignKey('tbl_writer.id'), nullable=False)
    writer = relationship("Writer", back_populates="tweets")

    comments = relationship("Comment", back_populates="tweet")

    stickers = relationship("Sticker", secondary=tweet_sticker, back_populates="tweets")


class Comment(Base):
    __tablename__ = "tbl_comment"
    id = Column(Integer, primary_key=True, autoincrement=True, nullable=False)

    # Исправлено здесь:
    tweetId = Column("tweet_id", Integer, ForeignKey('tbl_tweet.id'), nullable=True)
    tweet = relationship("Tweet", back_populates="comments")

    content = Column(String(2048), nullable=False)


class Sticker(Base):
    __tablename__ = "tbl_sticker"
    id = Column(Integer, primary_key=True, autoincrement=True, nullable=False)
    name = Column(String(32), nullable=False)

    tweets = relationship("Tweet", secondary=tweet_sticker, back_populates="stickers")