from sqlalchemy import Column, Integer, ForeignKey, Table, String
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.db.database import Base
from typing import List, TYPE_CHECKING

if TYPE_CHECKING:
    from app.models.user import User
    from app.models.comment import Comment
    from app.models.mark import Mark

topic_mark_association = Table(
    'tbl_topic_mark',
    Base.metadata,
    Column('topic_id', Integer, ForeignKey(
        'tbl_topic.id', ondelete="CASCADE"), primary_key=True),
    Column('mark_id', Integer, ForeignKey(
        'tbl_mark.id', ondelete="CASCADE"), primary_key=True)
)


class Topic(Base):
    __tablename__ = "tbl_topic"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    title: Mapped[str] = mapped_column(String, unique=True, index=True)
    content: Mapped[str] = mapped_column(String)
    user_id: Mapped[int] = mapped_column(
        ForeignKey("tbl_user.id", ondelete="CASCADE"))

    user: Mapped["User"] = relationship(back_populates="topics")
    comments: Mapped[List["Comment"]] = relationship(
        back_populates="topic", cascade="all, delete-orphan")
    marks: Mapped[List["Mark"]] = relationship(
        secondary=topic_mark_association, back_populates="topics")
