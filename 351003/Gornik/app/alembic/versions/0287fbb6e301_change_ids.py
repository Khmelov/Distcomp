"""change ids

Revision ID: 0287fbb6e301
Revises: 9a4fd6f5be88
Create Date: 2026-01-31 16:08:28.396334

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '0287fbb6e301'
down_revision: Union[str, Sequence[str], None] = '9a4fd6f5be88'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.alter_column('tweets', 'writer_id', new_column_name='writerId')
    op.alter_column('comments', 'tweet_id', new_column_name='tweetId')


def downgrade() -> None:
    op.alter_column('tweets', 'writerId', new_column_name='writer_id')
    op.alter_column('comments', 'tweetId', new_column_name='tweet_id')
