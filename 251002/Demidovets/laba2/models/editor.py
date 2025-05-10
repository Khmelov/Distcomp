from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Text
from .base import Base


class Editor(Base):
    __tablename__ = 'tbl_editor'

    login: Mapped[str] = mapped_column(Text, nullable = False, unique = True)
    password: Mapped[str] = mapped_column(Text, nullable = False)
    firstname: Mapped[str] = mapped_column(Text, nullable = False)
    lastname: Mapped[str] = mapped_column(Text, nullable = False)
   
    # Связь один ко многим с таблицей News
    news = relationship("News", back_populates="editor")