from sqlalchemy import Column, BigInteger, String, DateTime, ForeignKey
from app.core.database import Base

class Tweet(Base):
    __tablename__ = "tbl_tweet"
    __table_args__ = {"schema": "distcomp"}

    id = Column(BigInteger, primary_key=True, index=True)
    author_id = Column(BigInteger, ForeignKey("distcomp.tbl_author.id", ondelete="CASCADE"), nullable=False)
    title = Column(String(64), nullable=False)
    content = Column(String(2048), nullable=False)
    created = Column(DateTime, nullable=False)
    modified = Column(DateTime, nullable=False)