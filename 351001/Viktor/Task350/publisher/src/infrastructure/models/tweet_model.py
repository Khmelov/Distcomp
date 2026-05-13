from __future__ import annotations

from datetime import datetime
from sqlalchemy import String, Text, ForeignKey, func
from sqlalchemy.orm import Mapped, mapped_column, relationship

from Task350.publisher.src.infrastructure.database import Base


class TweetModel(Base):
    __tablename__ = "tbl_tweet"
    __table_args__ = {"schema": "distcomp"}

    id: Mapped[int] = mapped_column(primary_key=True)
    title: Mapped[str] = mapped_column(String(64), nullable=False)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    creator_id: Mapped[int] = mapped_column(
        ForeignKey("distcomp.tbl_creator.id", ondelete="CASCADE"),
        nullable=False
    )
    created_at: Mapped[datetime] = mapped_column(server_default=func.now())
    updated_at: Mapped[datetime] = mapped_column(server_default=func.now(), onupdate=func.now())

    # Отношения
    creator: Mapped["CreatorModel"] = relationship(back_populates="tweets")
    posts: Mapped[list["PostModel"]] = relationship(back_populates="tweet")
    markers: Mapped[list["MarkerModel"]] = relationship(
        secondary="distcomp.tbl_tweet_marker",
        back_populates="tweets"
    )