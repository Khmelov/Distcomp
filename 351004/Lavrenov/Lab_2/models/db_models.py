import datetime
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, UniqueConstraint
from sqlalchemy.orm import relationship
from sqlalchemy.dialects.postgresql import ARRAY
from db.database import Base


class TblUser(Base):
    __tablename__ = "tbl_user"
    __table_args__ = {"schema": "distcomp"}

    id = Column(Integer, primary_key=True, index=True)
    login = Column(String(64), unique=True, nullable=False)
    password = Column(String(128), nullable=False)
    firstname = Column(String(64), nullable=False)
    lastname = Column(String(64), nullable=False)


class TblMarker(Base):
    __tablename__ = "tbl_marker"
    __table_args__ = {"schema": "distcomp"}

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(32), unique=True, nullable=False)

    topic_markers = relationship(
        "TblTopicMarker", back_populates="marker", cascade="all, delete-orphan"
    )


class TblTopic(Base):
    __tablename__ = "tbl_topic"
    __table_args__ = {"schema": "distcomp"}

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, nullable=False)
    title = Column(String(64), unique=True, nullable=False)
    content = Column(String(2048), nullable=False)
    created = Column(DateTime, default=datetime.datetime.utcnow)
    modified = Column(
        DateTime, default=datetime.datetime.utcnow, onupdate=datetime.datetime.utcnow
    )

    topic_markers = relationship(
        "TblTopicMarker", back_populates="topic", cascade="all, delete-orphan"
    )


class TblTopicMarker(Base):
    __tablename__ = "tbl_topic_marker"
    __table_args__ = (
        UniqueConstraint("topic_id", "marker_id", name="uq_topic_marker"),
        {"schema": "distcomp"},
    )

    id = Column(Integer, primary_key=True, index=True)
    topic_id = Column(
        Integer, ForeignKey("distcomp.tbl_topic.id", ondelete="CASCADE"), nullable=False
    )
    marker_id = Column(
        Integer,
        ForeignKey("distcomp.tbl_marker.id", ondelete="CASCADE"),
        nullable=False,
    )

    topic = relationship("TblTopic", back_populates="topic_markers")
    marker = relationship("TblMarker", back_populates="topic_markers")


class TblNotice(Base):
    __tablename__ = "tbl_notice"
    __table_args__ = {"schema": "distcomp"}

    id = Column(Integer, primary_key=True, index=True)
    topic_id = Column(Integer, nullable=False)
    content = Column(String(2048), nullable=False)
    created = Column(DateTime, default=datetime.datetime.utcnow)
    modified = Column(
        DateTime, default=datetime.datetime.utcnow, onupdate=datetime.datetime.utcnow
    )
