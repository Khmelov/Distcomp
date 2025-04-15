from datetime import datetime, UTC
from typing import List, Optional
from sqlalchemy import ForeignKey, String, Text, DateTime, Table, Column, Integer
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship

from src.db.db import Base


class Creator(Base):
    __tablename__ = 'tbl_creator'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    login: Mapped[str] = mapped_column(String(64), unique=True, nullable=False)
    password: Mapped[str] = mapped_column(String(128), nullable=False)
    firstname: Mapped[str] = mapped_column(String(64), nullable=False)
    lastname: Mapped[str] = mapped_column(String(64), nullable=False)

    stories: Mapped[List["Story"]] = relationship(back_populates="creator")


class Story(Base):
    __tablename__ = 'tbl_story'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    creator_id: Mapped[int] = mapped_column(ForeignKey("tbl_creator.id"), nullable=False)
    title: Mapped[str] = mapped_column(String(64), unique=True, nullable=False)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    created: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(UTC),
        nullable=False
    )
    modified: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(UTC),
        onupdate=lambda: datetime.now(UTC),
        nullable=False
    )

    creator: Mapped["Creator"] = relationship(back_populates="stories")
    reactions: Mapped[List["Reaction"]] = relationship(back_populates="story")
    markers: Mapped[List["Marker"]] = relationship(
        secondary="tbl_story_marker",
        back_populates="stories"
    )


class Reaction(Base):
    __tablename__ = 'tbl_reaction'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    storyId: Mapped[int] = mapped_column(ForeignKey("tbl_story.id"), nullable=False)
    content: Mapped[str] = mapped_column(Text, nullable=False)

    story: Mapped["Story"] = relationship(back_populates="reactions")


class Marker(Base):
    __tablename__ = 'tbl_marker'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(32), unique=True, nullable=False)

    stories: Mapped[List["Story"]] = relationship(
        secondary="tbl_story_marker",
        back_populates="markers"
    )


tbl_story_marker = Table(
    'tbl_story_marker',
    Base.metadata,
    Column('storyId', ForeignKey('tbl_story.id'), primary_key=True),
    Column('markerId', ForeignKey('tbl_marker.id'), primary_key=True)
)