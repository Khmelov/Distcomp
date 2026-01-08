from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey, BigInteger
from .base import Base
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from .issue import Issue


class Comment(Base):

    __tablename__ = "tbl_comment"

    issueId: Mapped[int] = mapped_column(BigInteger, ForeignKey("tbl_issue.id"))
    content: Mapped[str] = mapped_column(Text, nullable=True)

    # Связь многие к одному с таблицей Issue
    issue: Mapped["Issue"] = relationship("Issue", back_populates="comments")
