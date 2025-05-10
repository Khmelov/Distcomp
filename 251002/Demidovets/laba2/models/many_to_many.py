from sqlalchemy import Table, ForeignKey, Column
from .base import Base



Sticker_news  = Table(
    "tbl_newssticker",
    Base.metadata,
    Column("newsId", ForeignKey("tbl_news.id")),
    Column("stickerId", ForeignKey("tbl_sticker.id")),
)



