from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey
from .base import Base
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from .news import News



class Comment(Base):

    __tablename__ = "comments"

    newsId: Mapped[int] = mapped_column(
        ForeignKey("news.id")
    )

    news: Mapped["News"] = relationship(back_populates="comments")

    content: Mapped[str] = mapped_column(Text, nullable = True)





