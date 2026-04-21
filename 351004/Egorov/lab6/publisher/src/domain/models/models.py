from __future__ import annotations

from datetime import datetime
from enum import Enum
from typing import List

from sqlalchemy import BigInteger, String, Text, ForeignKey, DateTime, Table, Column, func
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship


class UserRole(str, Enum):
    ADMIN = "ADMIN"
    CUSTOMER = "CUSTOMER"

class Base(DeclarativeBase):
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    pass


class Author(Base):
    __tablename__ = "tbl_author"

    login: Mapped[str] = mapped_column(String(64),nullable=False,unique=True)
    password: Mapped[str] = mapped_column(String,nullable=False)
    firstname: Mapped[str] = mapped_column(String(64),nullable=False)
    lastname: Mapped[str] = mapped_column(String(64),nullable=False)
    role: Mapped[UserRole] = mapped_column(nullable=False)

topic_tag_association = Table(
    "tbl_topic_tag",
    Base.metadata,
    Column("id", BigInteger, primary_key=True, autoincrement=True),
    Column("topicId", ForeignKey("tbl_topic.id"), nullable=False),
    Column("tagId", ForeignKey("tbl_tag.id"), nullable=False),
)


class Topic(Base):
    __tablename__ = "tbl_topic"

    title: Mapped[str] = mapped_column(String(64), nullable=False, unique=True)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime, nullable=False, server_default=func.now())
    updated_at: Mapped[datetime | None] = mapped_column(
        DateTime,
        nullable=False,
        server_default=func.now(),
        server_onupdate=func.now()
    )

    author_id: Mapped[int] = mapped_column(ForeignKey("tbl_author.id"), nullable=False)
    tags: Mapped[List[Tag]] = relationship(
        secondary=topic_tag_association,
        back_populates="topics",
        lazy = "selectin",
    )


class Tag(Base):
    __tablename__ = "tbl_tag"

    name: Mapped[str] = mapped_column(String(32), nullable=False, unique=True)

    topics: Mapped[List[Topic]] = relationship(secondary=topic_tag_association, back_populates="tags")
