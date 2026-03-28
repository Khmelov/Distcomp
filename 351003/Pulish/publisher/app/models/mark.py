from sqlalchemy import String
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.db.database import Base
from app.models.topic import topic_mark_association
from typing import List, TYPE_CHECKING

if TYPE_CHECKING:
    from app.models.topic import Topic


class Mark(Base):
    __tablename__ = "tbl_mark"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    name: Mapped[str] = mapped_column(String, unique=True, index=True)

    topics: Mapped[List["Topic"]] = relationship(
        secondary=topic_mark_association, back_populates="marks")
