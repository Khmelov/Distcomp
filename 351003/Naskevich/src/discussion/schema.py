"""DDL для модуля discussion (Cassandra).

Ключ партиционирования — tweet_id: PRIMARY KEY ((tweet_id), id).
"""

from cassandra.cluster import Session


def ensure_keyspace(session: Session, keyspace: str) -> None:
    session.execute(
        f"""
        CREATE KEYSPACE IF NOT EXISTS {keyspace}
        WITH replication = {{'class': 'SimpleStrategy', 'replication_factor': 1}}
        """
    )
    session.set_keyspace(keyspace)


def _posts_has_state_column(session: Session) -> bool:
    ks = session.keyspace
    if not ks:
        return True
    rows = session.execute(
        """
        SELECT column_name FROM system_schema.columns
        WHERE keyspace_name = %s AND table_name = 'posts'
        """,
        (ks,),
    )
    return any(r.column_name == "state" for r in rows)


def ensure_tables(session: Session) -> None:
    session.execute(
        """
        CREATE TABLE IF NOT EXISTS posts (
            tweet_id bigint,
            id bigint,
            content text,
            PRIMARY KEY ((tweet_id), id)
        )
        WITH CLUSTERING ORDER BY (id ASC)
        """
    )
    session.execute(
        """
        CREATE TABLE IF NOT EXISTS posts_by_post_id (
            id bigint PRIMARY KEY,
            tweet_id bigint
        )
        """
    )
    if not _posts_has_state_column(session):
        session.execute("ALTER TABLE posts ADD state text")
