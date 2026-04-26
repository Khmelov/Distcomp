from sqlalchemy import String
from sqlalchemy.orm import Mapped, mapped_column, relationship
from Task350.publisher.src.infrastructure.database import Base

class CreatorModel(Base):
    __tablename__ = "tbl_creator"
    __table_args__ = {"schema": "distcomp"}

    id: Mapped[int] = mapped_column(primary_key=True)
    login: Mapped[str] = mapped_column(String(64), unique=True, nullable=False)
    password: Mapped[str] = mapped_column(String(128), nullable=False)
    firstname: Mapped[str] = mapped_column(String(64), nullable=False)
    lastname: Mapped[str] = mapped_column(String(64), nullable=False)

    tweets: Mapped[list["TweetModel"]] = relationship(back_populates="creator")