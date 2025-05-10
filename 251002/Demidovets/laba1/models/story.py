from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey, DateTime, func
from .base import Base
from typing import TYPE_CHECKING
from datetime import datetime

from sqlalchemy import Table, ForeignKey, Column

if TYPE_CHECKING:
    from .editor import Editor
    from .comments import Comment



association_table = Table(
    "association_table",
    Base.metadata,
    Column("news_id", ForeignKey("news.id")),
    Column("sticker_id", ForeignKey("stickers.id")),
)


class News(Base):

    __tablename__ = "news"

    editorId: Mapped[int] = mapped_column(
        ForeignKey("editors.id")
    )
    editor: Mapped["Editor"] = relationship(back_populates="news")



    title: Mapped[str] = mapped_column(Text, nullable = True)
    content: Mapped[str] = mapped_column(Text, nullable = True)
    created: Mapped[str] = mapped_column(Text, nullable = True)
    modified: Mapped[datetime] = mapped_column(DateTime, server_default=func.now(), default=datetime.now,)

    comments: Mapped[list["Comment"]] = relationship(back_populates="news")


    # many to many with sticker
    stickers: Mapped[list["Sticker"]] = relationship(secondary=association_table)



class Sticker(Base):

    __tablename__ = "stickers"

    name: Mapped[str] = mapped_column(Text, nullable = True)


    # many to many with news
    news: Mapped[list["News"]] = relationship(secondary=association_table)



