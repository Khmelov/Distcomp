from __future__ import annotations

from datetime import datetime, timezone

from sqlalchemy import Column, DateTime, ForeignKey, Integer, String, Table, Text
from sqlalchemy.orm import relationship

from app.db import Base

article_tag = Table(
    "tbl_article_tag",
    Base.metadata,
    Column("article_id", Integer, ForeignKey("tbl_article.id"), primary_key=True),
    Column("tag_id", Integer, ForeignKey("tbl_tag.id"), primary_key=True),
)


class Creator(Base):
    __tablename__ = "tbl_creator"

    id = Column(Integer, primary_key=True, autoincrement=True)
    login = Column(String(64), nullable=False, unique=True)
    password = Column(String(128), nullable=False)
    firstname = Column(String(64), nullable=False)
    lastname = Column(String(64), nullable=False)

    articles = relationship("Article", back_populates="creator", cascade="all, delete-orphan")


class Article(Base):
    __tablename__ = "tbl_article"

    id = Column(Integer, primary_key=True, autoincrement=True)
    creator_id = Column(Integer, ForeignKey("tbl_creator.id"), nullable=False, index=True)
    title = Column(String(64), nullable=False, unique=True)
    content = Column(Text, nullable=False)
    created = Column(DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))
    modified = Column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
        onupdate=lambda: datetime.now(timezone.utc),
    )

    creator = relationship("Creator", back_populates="articles")
    tags = relationship("Tag", secondary=article_tag, back_populates="articles")
    messages = relationship("Message", back_populates="article", cascade="all, delete-orphan")


class Tag(Base):
    __tablename__ = "tbl_tag"

    id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String(32), nullable=False, unique=True)

    articles = relationship("Article", secondary=article_tag, back_populates="tags")


class Message(Base):
    __tablename__ = "tbl_message"

    id = Column(Integer, primary_key=True, autoincrement=True)
    article_id = Column(Integer, ForeignKey("tbl_article.id"), nullable=False, index=True)
    content = Column(Text, nullable=False)

    article = relationship("Article", back_populates="messages")
