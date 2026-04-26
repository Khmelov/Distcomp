from __future__ import annotations

from datetime import datetime, timezone
from typing import List

from sqlalchemy import (
    BigInteger,
    DateTime,
    ForeignKey,
    MetaData,
    String,
    Table,
    Column,
    UniqueConstraint,
)
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship

SCHEMA = "distcomp"


class Base(DeclarativeBase):
    metadata = MetaData(schema=SCHEMA)


tbl_news_label = Table(
    "tbl_news_label",
    Base.metadata,
    Column(
        "news_id",
        ForeignKey(f"{SCHEMA}.tbl_news.id", ondelete="CASCADE"),
        primary_key=True,
    ),
    Column(
        "label_id",
        ForeignKey(f"{SCHEMA}.tbl_label.id", ondelete="CASCADE"),
        primary_key=True,
    ),
)


class Writer(Base):
    __tablename__ = "tbl_writer"
    __table_args__ = (UniqueConstraint("login", name="uq_tbl_writer_login"),)

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    login: Mapped[str] = mapped_column(String(64), nullable=False)
    password: Mapped[str] = mapped_column(String(128), nullable=False)
    firstname: Mapped[str] = mapped_column(String(64), nullable=False)
    lastname: Mapped[str] = mapped_column(String(64), nullable=False)

    news: Mapped[List["News"]] = relationship(back_populates="writer")


class News(Base):
    __tablename__ = "tbl_news"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    writer_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey(f"{SCHEMA}.tbl_writer.id", ondelete="RESTRICT"), nullable=False
    )
    title: Mapped[str] = mapped_column(String(64), nullable=False)
    content: Mapped[str] = mapped_column(String(2048), nullable=False)
    created: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    modified: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)

    writer: Mapped["Writer"] = relationship(back_populates="news")
    labels: Mapped[List["Label"]] = relationship(
        secondary=tbl_news_label,
        back_populates="news_list",
    )
    notes: Mapped[List["Note"]] = relationship(
        back_populates="news",
        cascade="all, delete-orphan",
    )


class Label(Base):
    __tablename__ = "tbl_label"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(32), nullable=False)

    news_list: Mapped[List["News"]] = relationship(
        secondary=tbl_news_label,
        back_populates="labels",
    )


class Note(Base):
    __tablename__ = "tbl_note"

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    news_id: Mapped[int] = mapped_column(
        BigInteger, ForeignKey(f"{SCHEMA}.tbl_news.id", ondelete="CASCADE"), nullable=False
    )
    content: Mapped[str] = mapped_column(String(2048), nullable=False)

    news: Mapped["News"] = relationship(back_populates="notes")


def utcnow() -> datetime:
    return datetime.now(timezone.utc)
