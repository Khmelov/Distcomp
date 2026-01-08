from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey, DateTime, func, BigInteger
from .base import Base
from typing import TYPE_CHECKING
from datetime import datetime


if TYPE_CHECKING:
    from .writer import Writer


class Issue(Base):
    __tablename__ = "tbl_issue"

    writer_id: Mapped[int] = mapped_column(BigInteger, ForeignKey("tbl_writer.id"))
    title: Mapped[str] = mapped_column(Text, nullable=False)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    created: Mapped[str] = mapped_column(
        DateTime, server_default=func.now(), default=datetime.now
    )
    modified: Mapped[datetime] = mapped_column(
        DateTime,
        server_default=func.now(),
        default=datetime.now,
    )

    # Связь многие к одному с таблицей Writer
    writer: Mapped["Writer"] = relationship("Writer", back_populates="issues")

    # Связь один ко многим с таблицей Comment
    comments = relationship("Comment", back_populates="issue")
