from sqlalchemy import Column, BigInteger, String, ForeignKey
from app.core.database import Base

class Notice(Base):
    __tablename__ = "tbl_notice"
    __table_args__ = {"schema": "distcomp"}

    id = Column(BigInteger, primary_key=True, index=True)
    tweet_id = Column(BigInteger, ForeignKey("distcomp.tbl_tweet.id"), nullable=False)
    content = Column(String(2048), nullable=False)