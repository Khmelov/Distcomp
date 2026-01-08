from sqlalchemy.orm import DeclarativeBase, declared_attr
from sqlalchemy.orm import Mapped
from sqlalchemy.orm import mapped_column
from sqlalchemy import BigInteger


class Base(DeclarativeBase):
    abstract = True  # Указывает на то, что данная модель не создается в БД

    # @declared_attr
    # def tablename(cls) -> str:
    #     return f"{cls.name.lower()}s"

    id: Mapped[int] = mapped_column(BigInteger,primary_key=True)