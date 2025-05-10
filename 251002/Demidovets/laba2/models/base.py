from sqlalchemy.orm import DeclarativeBase
from sqlalchemy.orm import Mapped
from sqlalchemy.orm import mapped_column
from sqlalchemy import BigInteger


class Base(DeclarativeBase):
    abstract = True  # Указывает на то, что данная модель не создается в БД
    id: Mapped[int] = mapped_column(BigInteger,primary_key=True, autoincrement=True, nullable=True)