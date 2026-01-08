from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy import Text
from .base import Base


class Marker(Base):

    __tablename__ = "tbl_marker"
    name: Mapped[str] = mapped_column(Text, nullable=True)
