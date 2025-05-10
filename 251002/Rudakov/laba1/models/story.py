from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey, DateTime, func
from .base import Base
from typing import TYPE_CHECKING
from datetime import datetime

from sqlalchemy import Table, ForeignKey, Column

if TYPE_CHECKING:
    from .creator import Creator
    from .messages import Message



association_table = Table(
    "association_table",
    Base.metadata,
    Column("story_id", ForeignKey("stories.id")),
    Column("sticker_id", ForeignKey("stickers.id")),
)


class Story(Base):

    __tablename__ = "stories"

    creatorId: Mapped[int] = mapped_column(
        ForeignKey("creators.id")
    )
    creator: Mapped["Creator"] = relationship(back_populates="stories")



    title: Mapped[str] = mapped_column(Text, nullable = True)
    content: Mapped[str] = mapped_column(Text, nullable = True)
    created: Mapped[str] = mapped_column(Text, nullable = True)
    modified: Mapped[datetime] = mapped_column(DateTime, server_default=func.now(), default=datetime.now,)

    messages: Mapped[list["Message"]] = relationship(back_populates="story")


    # many to many with sticker
    stickers: Mapped[list["Sticker"]] = relationship(secondary=association_table)



class Sticker(Base):

    __tablename__ = "stickers"

    name: Mapped[str] = mapped_column(Text, nullable = True)


    # many to many with story
    stories: Mapped[list["Story"]] = relationship(secondary=association_table)



