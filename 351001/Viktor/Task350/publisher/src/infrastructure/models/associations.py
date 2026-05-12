from sqlalchemy import Table, Column, Integer, ForeignKey
from Task350.publisher.src.infrastructure.database import Base

tweet_marker_association = Table(
    "tbl_tweet_marker",
    Base.metadata,
    Column("tweet_id", Integer, ForeignKey("distcomp.tbl_tweet.id", ondelete="CASCADE"), primary_key=True),
    Column("marker_id", Integer, ForeignKey("distcomp.tbl_marker.id", ondelete="CASCADE"), primary_key=True),
    schema="distcomp"
)

