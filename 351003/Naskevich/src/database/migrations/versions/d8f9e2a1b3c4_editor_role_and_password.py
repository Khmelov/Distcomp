"""editor role + password length for bcrypt

Revision ID: d8f9e2a1b3c4
Revises: b3a1c0d9e8f7
Create Date: 2026-02-01

"""

from typing import Sequence, Union

import sqlalchemy as sa
from alembic import op


revision: str = "d8f9e2a1b3c4"
down_revision: Union[str, Sequence[str], None] = "b3a1c0d9e8f7"
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.alter_column(
        "tbl_editor",
        "password",
        existing_type=sa.String(length=128),
        type_=sa.String(length=255),
        existing_nullable=False,
    )
    op.add_column(
        "tbl_editor",
        sa.Column(
            "role",
            sa.String(length=16),
            server_default="CUSTOMER",
            nullable=False,
        ),
    )


def downgrade() -> None:
    op.drop_column("tbl_editor", "role")
    op.alter_column(
        "tbl_editor",
        "password",
        existing_type=sa.String(length=255),
        type_=sa.String(length=128),
        existing_nullable=False,
    )
