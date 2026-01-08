from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey
from .base import Base
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from .issue import Issue



class Comment(Base):

    __tablename__ = "comments"

    issueId: Mapped[int] = mapped_column(
        ForeignKey("issues.id")
    )

    issue: Mapped["Issue"] = relationship(back_populates="comments")

    content: Mapped[str] = mapped_column(Text, nullable = True)





