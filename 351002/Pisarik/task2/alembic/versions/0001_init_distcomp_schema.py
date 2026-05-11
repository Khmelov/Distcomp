"""init tables

Revision ID: 0001
Revises: 
Create Date: 2026-05-08
"""

from alembic import op
import sqlalchemy as sa


revision = "0001"
down_revision = None
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.create_table(
        "tbl_author",
        sa.Column("id", sa.BigInteger(), primary_key=True, autoincrement=True),
        sa.Column("login", sa.String(length=64), nullable=False, unique=True),
        sa.Column("password", sa.String(length=128), nullable=False),
        sa.Column("firstname", sa.String(length=64), nullable=False),
        sa.Column("lastname", sa.String(length=64), nullable=False),
    )

    op.create_table(
        "tbl_mark",
        sa.Column("id", sa.BigInteger(), primary_key=True, autoincrement=True),
        sa.Column("name", sa.String(length=32), nullable=False, unique=True),
    )

    op.create_table(
        "tbl_news",
        sa.Column("id", sa.BigInteger(), primary_key=True, autoincrement=True),
        sa.Column("author_id", sa.BigInteger(), sa.ForeignKey("tbl_author.id", ondelete="RESTRICT"), nullable=False),
        sa.Column("title", sa.String(length=64), nullable=False, unique=True),
        sa.Column("content", sa.Text(), nullable=False),
        sa.Column("created", sa.DateTime(), nullable=False),
        sa.Column("modified", sa.DateTime(), nullable=False),
    )

    op.create_table(
        "tbl_news_mark",
        sa.Column("id", sa.BigInteger(), primary_key=True, autoincrement=True),
        sa.Column("news_id", sa.BigInteger(), sa.ForeignKey("tbl_news.id", ondelete="CASCADE"), nullable=False),
        sa.Column("mark_id", sa.BigInteger(), sa.ForeignKey("tbl_mark.id", ondelete="CASCADE"), nullable=False),
        sa.UniqueConstraint("news_id", "mark_id", name="uq_news_mark_pair"),
    )

    op.create_table(
        "tbl_message",
        sa.Column("id", sa.BigInteger(), primary_key=True, autoincrement=True),
        sa.Column("news_id", sa.BigInteger(), sa.ForeignKey("tbl_news.id", ondelete="CASCADE"), nullable=False),
        sa.Column("content", sa.String(length=2048), nullable=False),
    )

    # seed: first Author must exist
    op.execute(
        """
        INSERT INTO tbl_author (login, password, firstname, lastname)
        VALUES ('romusan1@yandex.by', 'password123', 'Роман', 'Писарик')
        ON CONFLICT (login) DO NOTHING
        """
    )


def downgrade() -> None:
    op.drop_table("tbl_message")
    op.drop_table("tbl_news_mark")
    op.drop_table("tbl_news")
    op.drop_table("tbl_mark")
    op.drop_table("tbl_author")

