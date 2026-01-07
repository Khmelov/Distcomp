"""Create initial tables

Revision ID: 001_initial
Revises: 
Create Date: 2026-01-03 15:00:00.000000

"""
from alembic import op
import sqlalchemy as sa

revision = '001_initial'
down_revision = None
branch_labels = None
depends_on = None

def upgrade() -> None:
    op.create_table(
        'tbl_user',
        sa.Column('id', sa.BigInteger(), autoincrement=True, nullable=False),
        sa.Column('login', sa.Text(), nullable=False),
        sa.Column('password', sa.Text(), nullable=False),
        sa.Column('firstname', sa.Text(), nullable=False, server_default=''),
        sa.Column('lastname', sa.Text(), nullable=False, server_default=''),
        sa.PrimaryKeyConstraint('id'),
        sa.UniqueConstraint('login')
    )

    op.create_table(
        'tbl_issue',
        sa.Column('id', sa.BigInteger(), autoincrement=True, nullable=False),
        sa.Column('user_id', sa.BigInteger(), nullable=False),
        sa.Column('title', sa.Text(), nullable=False),
        sa.Column('content', sa.Text(), nullable=False),
        sa.Column('created', sa.TIMESTAMP(timezone=True), nullable=False),
        sa.Column('modified', sa.TIMESTAMP(timezone=True), nullable=False),
        sa.ForeignKeyConstraint(['user_id'], ['tbl_user.id'], ),
        sa.PrimaryKeyConstraint('id'),
        sa.UniqueConstraint('title')
    )

    op.create_table(
        'tbl_note',
        sa.Column('id', sa.BigInteger(), autoincrement=True, nullable=False),
        sa.Column('issue_id', sa.BigInteger(), nullable=False),
        sa.Column('content', sa.Text(), nullable=False),
        sa.ForeignKeyConstraint(['issue_id'], ['tbl_issue.id'], ),
        sa.PrimaryKeyConstraint('id')
    )

    op.create_table(
        'tbl_marker',
        sa.Column('id', sa.BigInteger(), autoincrement=True, nullable=False),
        sa.Column('name', sa.Text(), nullable=False),
        sa.PrimaryKeyConstraint('id')
    )

    op.create_table(
        'tbl_issue_marker',
        sa.Column('id', sa.BigInteger(), autoincrement=True, nullable=False),
        sa.Column('issue_id', sa.BigInteger(), nullable=False),
        sa.Column('marker_id', sa.BigInteger(), nullable=False),
        sa.ForeignKeyConstraint(['issue_id'], ['tbl_issue.id'], ),
        sa.ForeignKeyConstraint(['marker_id'], ['tbl_marker.id'], ),
        sa.PrimaryKeyConstraint('id')
    )

def downgrade() -> None:
    op.drop_table('tbl_issue_marker')
    op.drop_table('tbl_marker')
    op.drop_table('tbl_note')
    op.drop_table('tbl_issue')
    op.drop_table('tbl_user')
