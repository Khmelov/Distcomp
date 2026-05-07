from sqlalchemy import Column, BigInteger, String
from app.core.database import Base

class Author(Base):
    __tablename__ = "tbl_author"
    __table_args__ = {"schema": "distcomp"}

    id = Column(BigInteger, primary_key=True, index=True)
    login = Column(String(64), nullable=False)
    password = Column(String(128), nullable=False)
    firstname = Column(String(64), nullable=False)
    lastname = Column(String(64), nullable=False)