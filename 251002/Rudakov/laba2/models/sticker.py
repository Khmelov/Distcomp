from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy import Text
from .base import Base


class Sticker(Base):

    __tablename__ = "tbl_sticker"
    name: Mapped[str] = mapped_column(Text, nullable=True)
