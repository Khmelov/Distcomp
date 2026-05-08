from datetime import datetime, timezone

from sqlalchemy import (
    BigInteger,
    Column,
    ForeignKey,
    MetaData,
    String,
    Table,
    DateTime,
)
from sqlalchemy.orm import registry, relationship

from sqlalchemy import Enum as SqlEnum

from src.models.editor import Editor
from src.models.user_role import UserRole
from src.models.marker import Marker
from src.models.tweet import Tweet

metadata = MetaData()
mapper_registry = registry()

editors_table = Table(
    "tbl_editor",
    metadata,
    Column("id", BigInteger, primary_key=True, autoincrement=True),
    Column("login", String(64), nullable=False, unique=True),
    Column("password", String(255), nullable=False),
    Column("firstname", String(64), nullable=False),
    Column("lastname", String(64), nullable=False),
    Column(
        "role",
        SqlEnum(
            UserRole,
            values_callable=lambda m: [i.value for i in m],
            native_enum=False,
            length=16,
        ),
        nullable=False,
        server_default=UserRole.CUSTOMER.value,
    ),
)

tweet_markers_table = Table(
    "tbl_tweet_marker",
    metadata,
    Column("tweet_id", BigInteger, ForeignKey("tbl_tweet.id", ondelete="CASCADE"), primary_key=True),
    Column("marker_id", BigInteger, ForeignKey("tbl_marker.id", ondelete="CASCADE"), primary_key=True),
)

tweets_table = Table(
    "tbl_tweet",
    metadata,
    Column("id", BigInteger, primary_key=True, autoincrement=True),
    Column("editor_id", BigInteger, ForeignKey("tbl_editor.id", ondelete="CASCADE"), nullable=False),
    Column("title", String(64), nullable=False),
    Column("content", String(2048), nullable=False),
    Column("created", DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc)),
    Column("modified", DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc)),
)

markers_table = Table(
    "tbl_marker",
    metadata,
    Column("id", BigInteger, primary_key=True, autoincrement=True),
    Column("name", String(32), nullable=False, unique=True),
)

def run_mappers() -> None:
    if mapper_registry.mappers:
        return

    mapper_registry.map_imperatively(Editor, editors_table)

    mapper_registry.map_imperatively(
        Marker,
        markers_table,
    )

    mapper_registry.map_imperatively(
        Tweet,
        tweets_table,
        properties={
            "markers": relationship(
                Marker,
                secondary=tweet_markers_table,
                backref="tweets",
                lazy="selectin",
            ),
        },
    )

