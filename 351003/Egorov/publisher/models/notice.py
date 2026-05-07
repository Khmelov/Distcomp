from datetime import datetime

from sqlalchemy import BigInteger, DateTime, ForeignKey, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from publisher.database import Base


class Notice(Base):
    __tablename__ = "tbl_notice"
    __table_args__ = {"schema": "distcomp"}

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, index=True, autoincrement=True)
    content: Mapped[str] = mapped_column(String, nullable=False)
    story_id: Mapped[int] = mapped_column(
        BigInteger,
        ForeignKey("distcomp.tbl_story.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=datetime.utcnow,
    )

    story: Mapped["Story"] = relationship("Story", back_populates="notices")

