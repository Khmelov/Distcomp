from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey, BigInteger
from .base import Base
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from .story import Story


class Message(Base):

    __tablename__ = "tbl_message"

    storyId: Mapped[int] = mapped_column(BigInteger, ForeignKey("tbl_story.id"))
    content: Mapped[str] = mapped_column(Text, nullable=True)

    # Связь многие к одному с таблицей Story
    story: Mapped["Story"] = relationship("Story", back_populates="messages")
