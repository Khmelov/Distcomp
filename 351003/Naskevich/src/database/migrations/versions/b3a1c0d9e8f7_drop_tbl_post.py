"""drop tbl_post (posts moved to Cassandra / discussion module)

Revision ID: b3a1c0d9e8f7
Revises: 4e9ee2df7c29
Create Date: 2026-04-05

"""

from typing import Sequence, Union

from alembic import op


revision: str = "b3a1c0d9e8f7"
down_revision: Union[str, Sequence[str], None] = "4e9ee2df7c29"
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.drop_table("tbl_post")


def downgrade() -> None:
    op.create_table(
        "tbl_post",
        op.Column("id", op.BigInteger(), autoincrement=True, nullable=False),
        op.Column("tweet_id", op.BigInteger(), nullable=False),
        op.Column("content", op.String(length=2048), nullable=False),
        op.ForeignKeyConstraint(["tweet_id"], ["tbl_tweet.id"], ondelete="CASCADE"),
        op.PrimaryKeyConstraint("id"),
    )
