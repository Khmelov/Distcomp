from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey
from .base import Base
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from .story import Story



class Message(Base):

    __tablename__ = "messages"

    storyId: Mapped[int] = mapped_column(
        ForeignKey("stories.id")
    )

    story: Mapped["Story"] = relationship(back_populates="messages")

    content: Mapped[str] = mapped_column(Text, nullable = True)





