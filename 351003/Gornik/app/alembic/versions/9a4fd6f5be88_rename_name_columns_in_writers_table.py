"""rename name columns in writers table

Revision ID: 9a4fd6f5be88
Revises: 
Create Date: 2026-01-29 19:49:33.388526

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '9a4fd6f5be88'
down_revision: Union[str, Sequence[str], None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.alter_column('writers', 'first_name', new_column_name='firstname')
    op.alter_column('writers', 'last_name', new_column_name='lastname')

def downgrade() -> None:
    op.alter_column('writers', 'firstname', new_column_name='first_name')
    op.alter_column('writers', 'lastname', new_column_name='last_name')
