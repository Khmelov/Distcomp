from __future__ import annotations

from typing import Optional

from cassandra.cluster import Cluster, ConsistencyLevel, Session
from cassandra.policies import RoundRobinPolicy

from discussion_mod.core.settings import settings

_cluster: Optional[Cluster] = None
_session: Optional[Session] = None


def _apply_schema(sess: Session, keyspace: str) -> None:
    sess.execute(
        f"""
        CREATE KEYSPACE IF NOT EXISTS {keyspace}
        WITH replication = {{'class': 'SimpleStrategy', 'replication_factor': 1}};
        """
    )
    sess.set_keyspace(keyspace)
    sess.execute(
        f"""
        CREATE TABLE IF NOT EXISTS tbl_notes_by_news (
            news_id bigint,
            id bigint,
            content text,
            PRIMARY KEY ((news_id), id)
        );
        """
    )
    sess.execute(
        f"""
        CREATE TABLE IF NOT EXISTS tbl_note_by_id (
            id_bucket int,
            id bigint,
            news_id bigint,
            content text,
            PRIMARY KEY ((id_bucket), id)
        );
        """
    )


def init_db() -> Session:
    """
    Подключение к Cassandra и создание keyspace/таблиц (аналог миграций; см. db/cql и Liquibase).
    Схема distcomp; таблицы с префиксом tbl_.
    Партиционирование:
    - tbl_notes_by_news: ((news_id), id) — выборка заметок по новости без ALLOW FILTERING.
    - tbl_note_by_id: ((id_bucket), id), id_bucket = id % N — равномерное распределение
      точечных запросов по id (избегаем hot partition от монотонного id и тяжёлого secondary index).
    """
    global _cluster, _session
    if _session is not None:
        return _session

    load_balancing_policy = RoundRobinPolicy()
    _cluster = Cluster(
        settings.cassandra_hosts_list,
        port=settings.CASSANDRA_PORT,
        protocol_version=settings.CASSANDRA_PROTOCOL_VERSION,
        load_balancing_policy=load_balancing_policy,
    )
    _session = _cluster.connect()
    _session.default_consistency_level = ConsistencyLevel.ONE
    _apply_schema(_session, settings.CASSANDRA_KEYSPACE)
    return _session


def get_session() -> Session:
    if _session is None:
        raise RuntimeError("Cassandra session is not initialized")
    return _session


def close_db() -> None:
    global _cluster, _session
    if _session is not None:
        _session.shutdown()
        _session = None
    if _cluster is not None:
        _cluster.shutdown()
        _cluster = None
