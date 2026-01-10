from sqlalchemy import Column, BigInteger, Text, TIMESTAMP, ForeignKey, Table, UniqueConstraint
from sqlalchemy.orm import relationship, Mapped, mapped_column
from datetime import datetime
from app.core.db import Base

class UserEntity(Base):
    __tablename__ = "tbl_user"
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    login: Mapped[str] = mapped_column(Text, nullable=False, unique=True)
    password: Mapped[str] = mapped_column(Text, nullable=False)
    firstname: Mapped[str] = mapped_column(Text, default="", nullable=False)
    lastname: Mapped[str] = mapped_column(Text, default="", nullable=False)
    issues = relationship("IssueEntity", back_populates="user", cascade="all, delete-orphan")

class IssueEntity(Base):
    __tablename__ = "tbl_issue"
    __table_args__ = (
        UniqueConstraint('title', name='uq_issue_title'),
    )
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    user_id: Mapped[int] = mapped_column(BigInteger, ForeignKey("tbl_user.id"), nullable=False)
    title: Mapped[str] = mapped_column(Text, nullable=False)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    created: Mapped[datetime] = mapped_column(TIMESTAMP(timezone=True), default=datetime.utcnow, nullable=False)
    modified: Mapped[datetime] = mapped_column(TIMESTAMP(timezone=True), default=datetime.utcnow, nullable=False)
    user = relationship("UserEntity", back_populates="issues")
    notes = relationship("NoteEntity", back_populates="issue", cascade="all, delete-orphan")
    markers = relationship("MarkerEntity", secondary="tbl_issue_marker", back_populates="issues")

class NoteEntity(Base):
    __tablename__ = "tbl_note"
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    issue_id: Mapped[int] = mapped_column(BigInteger, ForeignKey("tbl_issue.id"), nullable=False)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    issue = relationship("IssueEntity", back_populates="notes")

class MarkerEntity(Base):
    __tablename__ = "tbl_marker"
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(Text, nullable=False)
    issues = relationship("IssueEntity", secondary="tbl_issue_marker", back_populates="markers")

class IssueMarkerEntity(Base):
    __tablename__ = "tbl_issue_marker"
    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True)
    issue_id: Mapped[int] = mapped_column(BigInteger, ForeignKey("tbl_issue.id"), nullable=False)
    marker_id: Mapped[int] = mapped_column(BigInteger, ForeignKey("tbl_marker.id"), nullable=False)


