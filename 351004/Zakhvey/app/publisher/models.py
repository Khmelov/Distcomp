from sqlalchemy import Column, String, BigInteger, DateTime, ForeignKey, Table
from sqlalchemy.orm import relationship
from database import Base

# Вспомогательная таблица для Many-to-Many
issue_label_table = Table(
    'tbl_issue_label',
    Base.metadata,
    Column('id', BigInteger, primary_key=True, autoincrement=True),
    Column('issue_id', BigInteger, ForeignKey('distcomp.tbl_issue.id', ondelete='CASCADE'), nullable=False),
    Column('label_id', BigInteger, ForeignKey('distcomp.tbl_label.id', ondelete='CASCADE'), nullable=False),
    schema='distcomp'
)

class User(Base):
    __tablename__ = 'tbl_user'
    __table_args__ = {'schema': 'distcomp'}

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    login = Column(String(64), unique=True, nullable=False)
    password = Column(String(128), nullable=False)
    firstname = Column(String(64), nullable=False)
    lastname = Column(String(64), nullable=False)

    issues = relationship("Issue", back_populates="user", cascade="all, delete-orphan")

class Issue(Base):
    __tablename__ = 'tbl_issue'
    __table_args__ = {'schema': 'distcomp'}

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    user_id = Column(BigInteger, ForeignKey('distcomp.tbl_user.id'), nullable=False)
    title = Column(String(64), nullable=False)
    content = Column(String(2048), nullable=False)
    created = Column(DateTime(timezone=True), nullable=False)
    modified = Column(DateTime(timezone=True), nullable=False)

    user = relationship("User", back_populates="issues")
    comments = relationship("Comment", back_populates="issue", cascade="all, delete-orphan")
    labels = relationship("Label", secondary=issue_label_table, back_populates="issues")

class Label(Base):
    __tablename__ = 'tbl_label'
    __table_args__ = {'schema': 'distcomp'}

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    name = Column(String(32), nullable=False, unique=True) # Сделали unique для поиска

    issues = relationship("Issue", secondary=issue_label_table, back_populates="labels")
