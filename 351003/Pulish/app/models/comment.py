from sqlalchemy import String, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.db.database import Base
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from app.models.topic import Topic


class Comment(Base):
    __tablename__ = "tbl_comment"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    content: Mapped[str] = mapped_column(String)
    topic_id: Mapped[int] = mapped_column(
        ForeignKey("tbl_topic.id", ondelete="CASCADE"))

    topic: Mapped["Topic"] = relationship(back_populates="comments")
