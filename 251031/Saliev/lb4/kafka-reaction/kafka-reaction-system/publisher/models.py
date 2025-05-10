from sqlalchemy import Column, Integer, String, ForeignKey, Enum
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class User(Base):
    __tablename__ = 'tbl_users'
    id = Column(Integer, primary_key=True)
    name = Column(String)
    news = relationship("News", back_populates="user")

class News(Base):
    __tablename__ = 'tbl_news'
    id = Column(Integer, primary_key=True)
    title = Column(String)
    content = Column(String)
    user_id = Column(Integer, ForeignKey('tbl_users.id'))
    user = relationship("User", back_populates="news")
    reactions = relationship("Reaction", back_populates="news")
    tags = relationship("Tag", secondary="tbl_news_tags")

class Tag(Base):
    __tablename__ = 'tbl_tags'
    id = Column(Integer, primary_key=True)
    name = Column(String)
    news = relationship("News", secondary="tbl_news_tags")

class Reaction(Base):
    __tablename__ = 'tbl_reactions'
    id = Column(Integer, primary_key=True)
    content = Column(String)
    state = Column(Enum('PENDING', 'APPROVE', 'DECLINE', name='reaction_state'))
    news_id = Column(Integer, ForeignKey('tbl_news.id'))
    news = relationship("News", back_populates="reactions")

# Association table for many-to-many relationship between News and Tag
class NewsTag(Base):
    __tablename__ = 'tbl_news_tags'
    news_id = Column(Integer, ForeignKey('tbl_news.id'), primary_key=True)
    tag_id = Column(Integer, ForeignKey('tbl_tags.id'), primary_key=True)