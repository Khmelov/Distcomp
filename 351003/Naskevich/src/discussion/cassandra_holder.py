from cassandra.cluster import Session

_session: Session | None = None


def set_cassandra_session(session: Session) -> None:
    global _session
    _session = session


def get_cassandra_session() -> Session:
    if _session is None:
        raise RuntimeError("Cassandra session is not initialized")
    return _session


def clear_cassandra_session() -> None:
    global _session
    _session = None
