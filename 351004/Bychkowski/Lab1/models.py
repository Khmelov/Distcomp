from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey, Table
from sqlalchemy.orm import relationship
from datetime import datetime, timezone
from database import Base

article_label_association = Table(
    'tbl_article_label',
    Base.metadata,
    Column('article_id', Integer, ForeignKey('distcomp.tbl_article.id', ondelete='CASCADE'), primary_key=True),
    Column('label_id', Integer, ForeignKey('distcomp.tbl_label.id', ondelete='CASCADE'), primary_key=True),
    schema='distcomp'
)

class Writer(Base):
    __tablename__ = 'tbl_writer'
    __table_args__ = {'schema': 'distcomp'}

    id = Column(Integer, primary_key=True, index=True)
    login = Column(String(64), unique=True, nullable=False)
    password = Column(String(128), nullable=False)
    firstname = Column(String(64), nullable=False)
    lastname = Column(String(64), nullable=False)

    articles = relationship("Article", back_populates="writer", cascade="all, delete", passive_deletes=True)


class Label(Base):
    __tablename__ = 'tbl_label'
    __table_args__ = {'schema': 'distcomp'}

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(32), unique=True, nullable=False)

    articles = relationship("Article", secondary=article_label_association, back_populates="labels")


class Article(Base):
    __tablename__ = 'tbl_article'
    __table_args__ = {'schema': 'distcomp'}

    id = Column(Integer, primary_key=True, index=True)
    writer_id = Column(Integer, ForeignKey('distcomp.tbl_writer.id', ondelete='CASCADE'), nullable=False)
    title = Column(String(64), unique=True, nullable=False)
    content = Column(Text, nullable=False)
    created = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    modified = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc))

    writer = relationship("Writer", back_populates="articles")
    posts = relationship("Post", back_populates="article", cascade="all, delete", passive_deletes=True)
    labels = relationship("Label", secondary=article_label_association, back_populates="articles")


class Post(Base):
    __tablename__ = 'tbl_post'
    __table_args__ = {'schema': 'distcomp'}

    id = Column(Integer, primary_key=True, index=True)
    article_id = Column(Integer, ForeignKey('distcomp.tbl_article.id', ondelete='CASCADE'), nullable=False)
    content = Column(Text, nullable=False)

    article = relationship("Article", back_populates="posts")