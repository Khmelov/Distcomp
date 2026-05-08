"""author role + bcrypt password length

Revision ID: 0002
Revises: 0001
"""

from __future__ import annotations

import bcrypt
import sqlalchemy as sa
from alembic import op
from sqlalchemy import text

revision = "0002"
down_revision = "0001"
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.add_column(
        "tbl_author",
        sa.Column("role", sa.String(length=32), nullable=False, server_default="CUSTOMER"),
    )
    op.alter_column(
        "tbl_author",
        "password",
        existing_type=sa.String(length=128),
        type_=sa.String(length=255),
        existing_nullable=False,
    )

    bind = op.get_bind()
    rows = bind.execute(text("SELECT id, password FROM tbl_author")).mappings().all()
    for r in rows:
        pw = (r["password"] or "").strip()
        if pw and not pw.startswith("$2"):
            hashed = bcrypt.hashpw(pw.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")
            bind.execute(text("UPDATE tbl_author SET password = :h WHERE id = :id"), {"h": hashed, "id": r["id"]})

    bind.execute(
        text("UPDATE tbl_author SET role = 'ADMIN' WHERE login = 'romanant@yandex.by'"),
    )


def downgrade() -> None:
    op.drop_column("tbl_author", "role")
