from __future__ import annotations

from sqlalchemy import String, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from Task320.src.infrastructure.database import Base


class PostModel(Base):
    __tablename__ = "tbl_post"
    __table_args__ = {"schema": "distcomp"}

    id: Mapped[int] = mapped_column(primary_key=True)
    content: Mapped[str] = mapped_column(String(2048), nullable=False)
    tweet_id: Mapped[int] = mapped_column(
        ForeignKey("distcomp.tbl_tweet.id", ondelete="CASCADE"),
        nullable=False
    )

    # Связь с TweetModel
    tweet: Mapped["TweetModel"] = relationship(back_populates="posts")