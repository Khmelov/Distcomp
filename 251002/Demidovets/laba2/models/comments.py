from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey, BigInteger
from .base import Base
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from .news import News


class Comment(Base):

    __tablename__ = "tbl_comment"

    newsId: Mapped[int] = mapped_column(BigInteger, ForeignKey("tbl_news.id"))
    content: Mapped[str] = mapped_column(Text, nullable=True)

    # Связь многие к одному с таблицей News
    news: Mapped["News"] = relationship("News", back_populates="comments")
