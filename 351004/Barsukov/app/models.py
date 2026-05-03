from sqlalchemy import Column, String, ForeignKey, Table, DateTime, BigInteger
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from database import Base

# Таблица связи Многие-ко-Многим (Issue <-> Sticker)
# В соответствии с ТЗ, идентификаторы заменены на BigInteger
issue_sticker = Table(
    'tbl_issue_sticker', Base.metadata,
    Column('issue_id', BigInteger, ForeignKey('tbl_issue.id', ondelete="CASCADE"), primary_key=True),
    Column('sticker_id', BigInteger, ForeignKey('tbl_sticker.id', ondelete="CASCADE"), primary_key=True)
)


class Author(Base):
    __tablename__ = "tbl_author"
    id = Column(BigInteger, primary_key=True, index=True)
    login = Column(String(64), unique=True, nullable=False)
    password = Column(String(128), nullable=False)
    firstname = Column(String(64), nullable=False)
    lastname = Column(String(64), nullable=False)
    role = Column(String(20), nullable=False, default="CUSTOMER")

    issues = relationship("Issue", back_populates="author")


class Issue(Base):
    __tablename__ = "tbl_issue"
    id = Column(BigInteger, primary_key=True, index=True)
    author_id = Column(BigInteger, ForeignKey("tbl_author.id", ondelete="CASCADE"), nullable=False)
    title = Column(String(64), nullable=False)
    content = Column(String(2048), nullable=False)
    created = Column(DateTime, server_default=func.now())
    modified = Column(DateTime, server_default=func.now(), onupdate=func.now())

    author = relationship("Author", back_populates="issues")

    # Связь многие-ко-многим через таблицу tbl_issue_sticker
    stickers = relationship("Sticker", secondary=issue_sticker)


class Sticker(Base):
    __tablename__ = "tbl_sticker"
    id = Column(BigInteger, primary_key=True, index=True)
    name = Column(String(32), unique=True, nullable=False)