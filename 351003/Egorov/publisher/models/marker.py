from datetime import datetime

from sqlalchemy import BigInteger, DateTime, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from publisher.database import Base


class Marker(Base):
    __tablename__ = "tbl_marker"
    __table_args__ = {"schema": "distcomp"}

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, index=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(255), unique=True, nullable=False, index=True)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=datetime.utcnow,
    )

    stories: Mapped[list["Story"]] = relationship(
        "Story",
        secondary="distcomp.tbl_story_marker",
        back_populates="markers",
    )

