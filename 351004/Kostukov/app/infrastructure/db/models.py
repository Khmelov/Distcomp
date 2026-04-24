from sqlalchemy import (
    Table, Column, BigInteger, Text, TIMESTAMP, ForeignKey
)
from sqlalchemy.orm import declarative_base, relationship
from sqlalchemy.sql import func

Base = declarative_base()

SCHEMA = "distcomp"


class Writer(Base):
    __tablename__ = "tbl_writer"
    __table_args__ = {"schema": SCHEMA}

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    login = Column(Text, nullable=False, unique=True)
    password = Column(Text, nullable=False)
    firstname = Column(Text)
    lastname = Column(Text)
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())

    articles = relationship(
        "Article",
        back_populates="writer",
        cascade="all, delete-orphan",
        passive_deletes=True,
    )


class Marker(Base):
    __tablename__ = "tbl_marker"
    __table_args__ = {"schema": SCHEMA}

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    name = Column(Text, nullable=False, unique=True)

    articles = relationship(
        "Article",
        secondary=lambda: article_marker,
        back_populates="markers",
    )


class Article(Base):
    __tablename__ = "tbl_article"
    __table_args__ = {"schema": SCHEMA}

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    writer_id = Column(
        BigInteger,
        ForeignKey(f"{SCHEMA}.tbl_writer.id", ondelete="CASCADE"),
        nullable=False,
    )
    title = Column(Text, nullable=False)
    content = Column(Text)
    created = Column(TIMESTAMP(timezone=True), server_default=func.now())
    modified = Column(TIMESTAMP(timezone=True), server_default=func.now(), onupdate=func.now())

    writer = relationship("Writer", back_populates="articles")

    notes = relationship(
        "Note",
        back_populates="article",
        cascade="all, delete-orphan",
        passive_deletes=True,
    )

    markers = relationship(
        "Marker",
        secondary=lambda: article_marker,
        back_populates="articles",
    )


class Note(Base):
    __tablename__ = "tbl_note"
    __table_args__ = {"schema": SCHEMA}

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    article_id = Column(
        BigInteger,
        ForeignKey(f"{SCHEMA}.tbl_article.id", ondelete="CASCADE"),
        nullable=False,
    )
    content = Column(Text, nullable=False)
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())

    article = relationship("Article", back_populates="notes")


article_marker = Table(
    "tbl_article_marker",
    Base.metadata,
    Column(
        "article_id",
        BigInteger,
        ForeignKey(f"{SCHEMA}.tbl_article.id", ondelete="CASCADE"),
        primary_key=True,
    ),
    Column(
        "marker_id",
        BigInteger,
        ForeignKey(f"{SCHEMA}.tbl_marker.id", ondelete="CASCADE"),
        primary_key=True,
    ),
    schema=SCHEMA,
)