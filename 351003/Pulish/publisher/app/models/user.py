from sqlalchemy import String
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.db.database import Base
from typing import List, TYPE_CHECKING

if TYPE_CHECKING:
    from app.models.topic import Topic


class User(Base):
    __tablename__ = "tbl_user"

    id: Mapped[int] = mapped_column(primary_key=True, index=True)
    login: Mapped[str] = mapped_column(String, unique=True, index=True)
    password: Mapped[str] = mapped_column(String)
    firstname: Mapped[str] = mapped_column(String)
    lastname: Mapped[str] = mapped_column(String)

    topics: Mapped[List["Topic"]] = relationship(
        back_populates="user", cascade="all, delete-orphan")
