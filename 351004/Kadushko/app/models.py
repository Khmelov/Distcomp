from sqlalchemy import Column, BigInteger, String, DateTime, ForeignKey, Table
from sqlalchemy.orm import relationship
from datetime import datetime
from app.database import Base

# Many-to-many association table for Issue and Marker
issue_marker = Table(
    "tbl_issue_marker",
    Base.metadata,
    Column("issue_id", BigInteger, ForeignKey("distcomp.tbl_issue.id"), primary_key=True),
    Column("marker_id", BigInteger, ForeignKey("distcomp.tbl_marker.id"), primary_key=True),
    schema="distcomp"
)


class Editor(Base):
    __tablename__ = "tbl_editor"
    __table_args__ = {"schema": "distcomp"}

    id = Column(BigInteger, primary_key=True, index=True, autoincrement=True)
    login = Column(String(64), unique=True, nullable=False)
    password = Column(String(128), nullable=False)
    firstname = Column(String(64), nullable=False)
    lastname = Column(String(64), nullable=False)

    issues = relationship("Issue", back_populates="editor", cascade="all, delete-orphan")


class Issue(Base):
    __tablename__ = "tbl_issue"
    __table_args__ = {"schema": "distcomp"}

    id = Column(BigInteger, primary_key=True, index=True, autoincrement=True)
    editor_id = Column(BigInteger, ForeignKey("distcomp.tbl_editor.id"), nullable=False)
    title = Column(String(64), unique=True, nullable=False)
    content = Column(String(2048), nullable=False)
    created = Column(DateTime, default=datetime.utcnow, nullable=False)
    modified = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)

    editor = relationship("Editor", back_populates="issues")
    comments = relationship("Comment", back_populates="issue", cascade="all, delete-orphan")
    markers = relationship("Marker", secondary=issue_marker, back_populates="issues")


class Marker(Base):
    __tablename__ = "tbl_marker"
    __table_args__ = {"schema": "distcomp"}

    id = Column(BigInteger, primary_key=True, index=True, autoincrement=True)
    name = Column(String(32), unique=True, nullable=False)

    issues = relationship("Issue", secondary=issue_marker, back_populates="markers")


class Comment(Base):
    __tablename__ = "tbl_comment"
    __table_args__ = {"schema": "distcomp"}

    id = Column(BigInteger, primary_key=True, index=True, autoincrement=True)
    issue_id = Column(BigInteger, ForeignKey("distcomp.tbl_issue.id"), nullable=False)
    content = Column(String(2048), nullable=False)

    issue = relationship("Issue", back_populates="comments")
