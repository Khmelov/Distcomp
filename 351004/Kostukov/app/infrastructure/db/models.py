from sqlalchemy import (
    Table, Column, BigInteger, Text, TIMESTAMP, ForeignKey
)
from sqlalchemy.orm import registry, relationship
from sqlalchemy.sql import func

mapper_registry = registry()
metadata = mapper_registry.metadata
metadata.schema = "distcomp"  # ensure schema used in queries

from sqlalchemy import Integer

from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base(metadata = metadata)

class Writer(Base):
    __tablename__ = "tbl_writer"
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    login = Column(Text, nullable=False, unique=True)
    password = Column(Text, nullable=False)
    firstname = Column(Text)
    lastname = Column(Text)
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())

class Marker(Base):
    __tablename__ = "tbl_marker"
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    name = Column(Text, nullable=False, unique=True)

class Article(Base):
    __tablename__ = "tbl_article"
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    writer_id = Column(BigInteger, ForeignKey("distcomp.tbl_writer.id"), nullable=False)
    title = Column(Text, nullable=False)
    content = Column(Text)
    created = Column(TIMESTAMP(timezone=True), server_default=func.now())
    modified = Column(TIMESTAMP(timezone=True), server_default=func.now(), onupdate=func.now())

class Note(Base):
    __tablename__ = "tbl_note"
    id = Column(BigInteger, primary_key=True, autoincrement=True)
    article_id = Column(BigInteger, ForeignKey("distcomp.tbl_article.id"), nullable=False)
    content = Column(Text, nullable=False)
    created_at = Column(TIMESTAMP(timezone=True), server_default=func.now())

# таблица для связи многие - ко многим(артикли - маркеры)
from sqlalchemy import Table
article_marker = Table(
    "tbl_article_marker", metadata,
    Column("article_id", BigInteger, ForeignKey("distcomp.tbl_article.id"), primary_key=True),
    Column("marker_id", BigInteger, ForeignKey("distcomp.tbl_marker.id"), primary_key=True),
    schema="distcomp"
)
