from sqlalchemy import Column, Integer, String, ForeignKey, DateTime, Table
from sqlalchemy.orm import relationship
from datetime import datetime
from database import Base

tweet_marker_table = Table(
    "tbl_tweet_marker",
    Base.metadata,
    Column("tweet_id", Integer, ForeignKey("tbl_tweet.id", ondelete="CASCADE"), primary_key=True),
    Column("marker_id", Integer, ForeignKey("tbl_marker.id", ondelete="CASCADE"), primary_key=True),
)


class Writer(Base):
    __tablename__ = "tbl_writer"
    id = Column(Integer, primary_key=True, index=True)
    login = Column(String(255), unique=True, index=True, nullable=False)
    password = Column(String(255), nullable=False)
    firstname = Column(String(255))
    lastname = Column(String(255))
    role = Column(String(50), default="CUSTOMER")   # ← добавь это
    tweets = relationship("Tweet", back_populates="writer", cascade="all, delete-orphan")
class Marker(Base):
    __tablename__ = "tbl_marker"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(32), unique=True, nullable=False)
    tweets = relationship("Tweet", secondary=tweet_marker_table, back_populates="markers")

class Tweet(Base):
    __tablename__ = "tbl_tweet"
    id = Column(Integer, primary_key=True, index=True)
    writer_id = Column(Integer, ForeignKey("tbl_writer.id", ondelete="CASCADE"), nullable=False)
    title = Column(String(64), nullable=False)
    content = Column(String(2048), nullable=False)
    created = Column(DateTime, default=datetime.utcnow)
    modified = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    writer = relationship("Writer", back_populates="tweets")
    comments = relationship("Comment", back_populates="tweet", cascade="all, delete-orphan")
    markers = relationship("Marker", secondary=tweet_marker_table, back_populates="tweets")

    @property
    def marker_ids(self):
        return [m.id for m in self.markers]

class Comment(Base):
    __tablename__ = "tbl_comment"
    id = Column(Integer, primary_key=True, index=True)
    tweet_id = Column(Integer, ForeignKey("tbl_tweet.id", ondelete="CASCADE"), nullable=False)
    content = Column(String(2048), nullable=False)
    tweet = relationship("Tweet", back_populates="comments")
