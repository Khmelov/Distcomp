from __future__ import annotations

from sqlalchemy import String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from Task320.src.infrastructure.database import Base


class MarkerModel(Base):
    __tablename__ = "tbl_marker"
    __table_args__ = {"schema": "distcomp"}

    id: Mapped[int] = mapped_column(primary_key=True)
    name: Mapped[str] = mapped_column(String(32), nullable=False, unique=True)

    # Связь многие-ко-многим с TweetModel
    tweets: Mapped[list["TweetModel"]] = relationship(
        secondary="distcomp.tbl_tweet_marker",
        back_populates="markers"
    )