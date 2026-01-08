from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text, ForeignKey, DateTime, func
from .base import Base
from typing import TYPE_CHECKING
from datetime import datetime

from sqlalchemy import Table, ForeignKey, Column

if TYPE_CHECKING:
    from .writer import Writer
    from .comments import Comment



association_table = Table(
    "association_table",
    Base.metadata,
    Column("issue_id", ForeignKey("issues.id")),
    Column("marker_id", ForeignKey("markers.id")),
)


class Issue(Base):

    __tablename__ = "issues"

    writerId: Mapped[int] = mapped_column(
        ForeignKey("writers.id")
    )
    writer: Mapped["Writer"] = relationship(back_populates="issues")



    title: Mapped[str] = mapped_column(Text, nullable = True)
    content: Mapped[str] = mapped_column(Text, nullable = True)
    created: Mapped[str] = mapped_column(Text, nullable = True)
    modified: Mapped[datetime] = mapped_column(DateTime, server_default=func.now(), default=datetime.now,)

    comments: Mapped[list["Comment"]] = relationship(back_populates="issue")


    # many to many with marker
    markers: Mapped[list["Marker"]] = relationship(secondary=association_table)



class Marker(Base):

    __tablename__ = "markers"

    name: Mapped[str] = mapped_column(Text, nullable = True)


    # many to many with issue
    issues: Mapped[list["Issue"]] = relationship(secondary=association_table)



