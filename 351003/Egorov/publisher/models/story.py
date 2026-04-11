from datetime import datetime

from sqlalchemy import BigInteger, DateTime, ForeignKey, String, Table, Column
from sqlalchemy.orm import Mapped, mapped_column, relationship

from publisher.database import Base, metadata


story_marker_table = Table(
    "tbl_story_marker",
    metadata,
    Column("story_id", BigInteger, ForeignKey("distcomp.tbl_story.id", ondelete="CASCADE"), primary_key=True),
    Column("marker_id", BigInteger, ForeignKey("distcomp.tbl_marker.id", ondelete="CASCADE"), primary_key=True),
)


class Story(Base):
    __tablename__ = "tbl_story"
    __table_args__ = {"schema": "distcomp"}

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, index=True, autoincrement=True)
    title: Mapped[str] = mapped_column(String(255), nullable=False)
    content: Mapped[str] = mapped_column(String, nullable=False)
    creator_id: Mapped[int] = mapped_column(
        BigInteger,
        ForeignKey("distcomp.tbl_creator.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=datetime.utcnow,
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=datetime.utcnow,
        onupdate=datetime.utcnow,
    )

    creator: Mapped["Creator"] = relationship("Creator", back_populates="stories")
    markers: Mapped[list["Marker"]] = relationship(
        "Marker",
        secondary=story_marker_table,
        back_populates="stories",
    )
    notices: Mapped[list["Notice"]] = relationship(
        "Notice",
        back_populates="story",
        cascade="all, delete-orphan",
    )

