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


class User(Base):
    __tablename__ = "tbl_user"

    login: Mapped[str] = mapped_column(String(64),nullable=False,unique=True)
    password: Mapped[str] = mapped_column(String,nullable=False)
    firstname: Mapped[str] = mapped_column(String(64),nullable=False)
    lastname: Mapped[str] = mapped_column(String(64),nullable=False)
    role: Mapped[UserRole] = mapped_column(nullable=False)

notice_label_association = Table(
    "tbl_notice_label",
    Base.metadata,
    Column("id", BigInteger, primary_key=True, autoincrement=True),
    Column("noticeId", ForeignKey("tbl_notice.id"), nullable=False),
    Column("labelId", ForeignKey("tbl_label.id"), nullable=False),
)


class Notice(Base):
    __tablename__ = "tbl_notice"

    title: Mapped[str] = mapped_column(String(64), nullable=False, unique=True)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime, nullable=False, server_default=func.now())
    updated_at: Mapped[datetime | None] = mapped_column(
        DateTime,
        nullable=False,
        server_default=func.now(),
        server_onupdate=func.now()
    )

    user_id: Mapped[int] = mapped_column(ForeignKey("tbl_user.id"), nullable=False)
    labels: Mapped[List[Label]] = relationship(
        secondary=notice_label_association,
        back_populates="notices",
        lazy = "selectin",
    )


class Label(Base):
    __tablename__ = "tbl_label"

    name: Mapped[str] = mapped_column(String(32), nullable=False, unique=True)

    notices: Mapped[List[Notice]] = relationship(secondary=notice_label_association, back_populates="labels")
