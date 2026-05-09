from __future__ import annotations

from datetime import datetime
from typing import List

from sqlalchemy import BigInteger, String, Text, ForeignKey, DateTime, Table, Column, func
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship


class Base(DeclarativeBase):
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    pass


class Writer(Base):
    __tablename__ = "tbl_writer"

    login: Mapped[str] = mapped_column(String(64),nullable=False,unique=True)
    password: Mapped[str] = mapped_column(String(128),nullable=False)
    firstname: Mapped[str] = mapped_column(String(64),nullable=False)
    lastname: Mapped[str] = mapped_column(String(64),nullable=False)

news_label_association = Table(
    "tbl_news_label",
    Base.metadata,
    Column("id", BigInteger, primary_key=True, autoincrement=True),
    Column("newsId", ForeignKey("tbl_news.id"), nullable=False),
    Column("labelId", ForeignKey("tbl_label.id"), nullable=False),
)


class News(Base):
    __tablename__ = "tbl_news"

    title: Mapped[str] = mapped_column(String(64), nullable=False, unique=True)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime, nullable=False, server_default=func.now())
    updated_at: Mapped[datetime | None] = mapped_column(
        DateTime,
        nullable=False,
        server_default=func.now(),
        server_onupdate=func.now()
    )

    writer_id: Mapped[int] = mapped_column(ForeignKey("tbl_writer.id"), nullable=False)
    labels: Mapped[List[Label]] = relationship(
        secondary=news_label_association,
        back_populates="news",
        lazy = "selectin",
    )


class Label(Base):
    __tablename__ = "tbl_label"

    name: Mapped[str] = mapped_column(String(32), nullable=False, unique=True)

    news: Mapped[List[News]] = relationship(secondary=news_label_association, back_populates="labels")
