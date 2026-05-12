import os
import random
import time
from typing import Optional, List

from cassandra.cluster import Cluster
from cassandra.policies import DCAwareRoundRobinPolicy

from .interface import NoteRepository, NoteDto, NoteState


def _generate_id() -> int:
    return (int(time.time() * 1_000_000) << 10) + random.randint(0, 1023)


class CassandraNoteRepository(NoteRepository):


    KEYSPACE = "distcomp"
    TABLE_NOTE = "tbl_note"
    TABLE_NOTE_BY_ID = "tbl_note_by_id"

    def __init__(self, host: str = "localhost", port: int = 9042):
        self._cluster = Cluster(
            [host],
            port=port,
            load_balancing_policy=DCAwareRoundRobinPolicy(local_dc="datacenter1"),
            connect_timeout=1.0,
        )
        self._session = self._cluster.connect()
        self._session.default_timeout = 1.0
        self._ensure_schema()

    def _ensure_schema(self) -> None:
        self._session.execute(
            f"""
            CREATE KEYSPACE IF NOT EXISTS {self.KEYSPACE}
            WITH REPLICATION = {{ 'class': 'SimpleStrategy', 'replication_factor': 1 }}
            AND DURABLE_WRITES = true
            """
        )
        self._session.set_keyspace(self.KEYSPACE)

        self._session.execute(
            f"""
            CREATE TABLE IF NOT EXISTS {self.TABLE_NOTE} (
                story_id bigint,
                id bigint,
                content text,
                country text,
                state text,
                PRIMARY KEY (story_id, id)
            )
            WITH CLUSTERING ORDER BY (id ASC)
            """
        )
        self._session.execute(
            f"""
            CREATE TABLE IF NOT EXISTS {self.TABLE_NOTE_BY_ID} (
                id bigint PRIMARY KEY,
                story_id bigint
            )
            """
        )

        self._insert_note = self._session.prepare(
            f"INSERT INTO {self.TABLE_NOTE} (story_id, id, content, country, state) VALUES (?, ?, ?, ?, ?)"
        )
        self._insert_by_id = self._session.prepare(
            f"INSERT INTO {self.TABLE_NOTE_BY_ID} (id, story_id) VALUES (?, ?)"
        )
        self._select_by_story = self._session.prepare(
            f"SELECT id, story_id, content, country, state FROM {self.TABLE_NOTE} WHERE story_id = ?"
        )
        self._select_by_id_lookup = self._session.prepare(
            f"SELECT story_id FROM {self.TABLE_NOTE_BY_ID} WHERE id = ?"
        )
        self._select_one = self._session.prepare(
            f"SELECT id, story_id, content, country, state FROM {self.TABLE_NOTE} WHERE story_id = ? AND id = ?"
        )
        self._update_note = self._session.prepare(
            f"UPDATE {self.TABLE_NOTE} SET content = ?, country = ?, state = ? WHERE story_id = ? AND id = ?"
        )
        self._delete_note = self._session.prepare(
            f"DELETE FROM {self.TABLE_NOTE} WHERE story_id = ? AND id = ?"
        )
        self._delete_by_id = self._session.prepare(
            f"DELETE FROM {self.TABLE_NOTE_BY_ID} WHERE id = ?"
        )

    def create(self, story_id: int, content: str, country: str = "",
               state: str = NoteState.PENDING, note_id: int = None) -> NoteDto:
        if note_id is None:
            note_id = _generate_id()
        self._session.execute(self._insert_note, (story_id, note_id, content, country or "", state))
        self._session.execute(self._insert_by_id, (note_id, story_id))
        return NoteDto(id=note_id, storyId=story_id, content=content, country=country or "", state=state)

    def get_by_id(self, note_id: int) -> Optional[NoteDto]:
        row = self._session.execute(self._select_by_id_lookup, (note_id,)).one()
        if not row:
            return None
        story_id = row.story_id
        row = self._session.execute(self._select_one, (story_id, note_id)).one()
        if not row:
            return None
        return NoteDto(id=row.id, storyId=row.story_id, content=row.content or "", country=row.country or "", state=row.state or NoteState.PENDING)

    def list_by_story(self, story_id: int) -> List[NoteDto]:
        rows = self._session.execute(self._select_by_story, (story_id,))
        return [
            NoteDto(id=r.id, storyId=r.story_id, content=r.content or "", country=r.country or "", state=r.state or NoteState.PENDING)
            for r in rows
        ]

    def update(
        self, story_id: int, note_id: int, content: str, country: str = "", state: str = None
    ) -> Optional[NoteDto]:
        existing = self.get_by_id(note_id)
        if not existing or existing.storyId != story_id:
            return None
        new_state = state if state else existing.state
        self._session.execute(
            self._update_note, (content, country or "", new_state, story_id, note_id)
        )
        return NoteDto(id=note_id, storyId=story_id, content=content, country=country or "", state=new_state)

    def delete(self, story_id: int, note_id: int) -> bool:
        existing = self.get_by_id(note_id)
        if not existing or existing.storyId != story_id:
            return False
        self._session.execute(self._delete_note, (story_id, note_id))
        self._session.execute(self._delete_by_id, (note_id,))
        return True
