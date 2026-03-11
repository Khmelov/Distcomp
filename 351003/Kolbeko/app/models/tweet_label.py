from sqlalchemy import Column, BigInteger, ForeignKey
from app.core.database import Base

class TweetLabel(Base):
    __tablename__ = "tbl_tweet_label"
    __table_args__ = {"schema": "distcomp"}

    id = Column(BigInteger, primary_key=True, index=True)
    tweet_id = Column(BigInteger, ForeignKey("distcomp.tbl_tweet.id"), nullable=False)
    label_id = Column(BigInteger, ForeignKey("distcomp.tbl_label.id"), nullable=False)