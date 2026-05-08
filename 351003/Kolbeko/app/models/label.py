from sqlalchemy import Column, BigInteger, String
from app.core.database import Base

class Label(Base):
    __tablename__ = "tbl_label"
    __table_args__ = {"schema": "distcomp"}

    id = Column(BigInteger, primary_key=True, index=True)
    name = Column(String(32), nullable=False)