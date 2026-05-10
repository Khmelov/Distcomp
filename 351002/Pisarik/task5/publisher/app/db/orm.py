from __future__ import annotations

from datetime import datetime

from sqlalchemy import (
    BigInteger,
    DateTime,
    ForeignKey,
    String,
    Text,
    UniqueConstraint,
)
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship

class Base(DeclarativeBase):
    pass


def _tbl(name: str) -> str:
    return f"tbl_{name}"


class AuthorOrm(Base):
    __tablename__ = _tbl("author")

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    login: Mapped[str] = mapped_column(String(64), unique=True, nullable=False)
    password: Mapped[str] = mapped_column(String(128), nullable=False)
    firstname: Mapped[str] = mapped_column(String(64), nullable=False)
    lastname: Mapped[str] = mapped_column(String(64), nullable=False)

    news: Mapped[list[NewsOrm]] = relationship(back_populates="author", cascade="all,delete-orphan")


class MarkOrm(Base):
    __tablename__ = _tbl("mark")

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(32), nullable=False, unique=True)

    news: Mapped[list[NewsOrm]] = relationship(
        secondary=_tbl("news_mark"),
        back_populates="marks",
    )


class NewsMarkOrm(Base):
    __tablename__ = _tbl("news_mark")
    __table_args__ = (
        UniqueConstraint("news_id", "mark_id", name="uq_news_mark_pair"),
    )

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    news_id: Mapped[int] = mapped_column(
        BigInteger,
        ForeignKey(f"{_tbl('news')}.id", ondelete="CASCADE"),
        nullable=False,
    )
    mark_id: Mapped[int] = mapped_column(
        BigInteger,
        ForeignKey(f"{_tbl('mark')}.id", ondelete="CASCADE"),
        nullable=False,
    )


class NewsOrm(Base):
    __tablename__ = _tbl("news")

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    author_id: Mapped[int] = mapped_column(
        BigInteger,
        ForeignKey(f"{_tbl('author')}.id", ondelete="RESTRICT"),
        nullable=False,
    )
    title: Mapped[str] = mapped_column(String(64), nullable=False, unique=True)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    created: Mapped[datetime] = mapped_column(DateTime(timezone=False), default=datetime.utcnow, nullable=False)
    modified: Mapped[datetime] = mapped_column(DateTime(timezone=False), default=datetime.utcnow, nullable=False)

    author: Mapped[AuthorOrm] = relationship(back_populates="news")
    marks: Mapped[list[MarkOrm]] = relationship(
        secondary=_tbl("news_mark"),
        back_populates="news",
    )

