from __future__ import annotations

from typing import List, Optional, Tuple

from cassandra.cluster import Session

from discussion_mod.core.errors import AppError
from discussion_mod.core.settings import settings


class NoteRepository:
    def __init__(self, session: Session):
        self._s = session
        self._buckets = settings.NOTE_ID_BUCKETS

    def _bucket(self, note_id: int) -> int:
        return int(note_id % self._buckets)

    def insert(self, news_id: int, note_id: int, content: str) -> None:
        b = self._bucket(note_id)
        self._s.execute(
            """
            INSERT INTO tbl_notes_by_news (news_id, id, content)
            VALUES (%s, %s, %s)
            """,
            (news_id, note_id, content),
        )
        self._s.execute(
            """
            INSERT INTO tbl_note_by_id (id_bucket, id, news_id, content)
            VALUES (%s, %s, %s, %s)
            """,
            (b, note_id, news_id, content),
        )

    def get_by_id(self, note_id: int) -> Optional[Tuple[int, int, str]]:
        row = self._s.execute(
            """
            SELECT id, news_id, content FROM tbl_note_by_id
            WHERE id_bucket = %s AND id = %s
            """,
            (self._bucket(note_id), note_id),
        ).one()
        if row is None:
            return None
        return int(row.id), int(row.news_id), str(row.content)

    def list_all(self) -> List[Tuple[int, int, str]]:
        out: List[Tuple[int, int, str]] = []
        for bucket in range(self._buckets):
            rows = self._s.execute(
                """
                SELECT id, news_id, content FROM tbl_note_by_id
                WHERE id_bucket = %s
                """,
                (bucket,),
            )
            for row in rows:
                out.append((int(row.id), int(row.news_id), str(row.content)))
        out.sort(key=lambda t: t[0])
        return out

    def list_by_news(self, news_id: int) -> List[Tuple[int, int, str]]:
        rows = self._s.execute(
            """
            SELECT id, news_id, content FROM tbl_notes_by_news
            WHERE news_id = %s
            """,
            (news_id,),
        )
        out = [(int(r.id), int(r.news_id), str(r.content)) for r in rows]
        out.sort(key=lambda t: t[0])
        return out

    def update(self, note_id: int, new_news_id: int, content: str) -> None:
        cur = self.get_by_id(note_id)
        if cur is None:
            raise AppError(404, 40404, "Note not found")
        _, old_news_id, _ = cur
        if old_news_id != new_news_id:
            self._s.execute(
                "DELETE FROM tbl_notes_by_news WHERE news_id = %s AND id = %s",
                (old_news_id, note_id),
            )
        self._s.execute(
            """
            INSERT INTO tbl_notes_by_news (news_id, id, content)
            VALUES (%s, %s, %s)
            """,
            (new_news_id, note_id, content),
        )
        self._s.execute(
            """
            INSERT INTO tbl_note_by_id (id_bucket, id, news_id, content)
            VALUES (%s, %s, %s, %s)
            """,
            (self._bucket(note_id), note_id, new_news_id, content),
        )

    def delete(self, note_id: int) -> None:
        cur = self.get_by_id(note_id)
        if cur is None:
            raise AppError(404, 40404, "Note not found")
        _, news_id, _ = cur
        self._s.execute(
            "DELETE FROM tbl_notes_by_news WHERE news_id = %s AND id = %s",
            (news_id, note_id),
        )
        self._s.execute(
            "DELETE FROM tbl_note_by_id WHERE id_bucket = %s AND id = %s",
            (self._bucket(note_id), note_id),
        )

    def delete_all_for_news(self, news_id: int) -> None:
        rows = self._s.execute(
            "SELECT id FROM tbl_notes_by_news WHERE news_id = %s",
            (news_id,),
        )
        for row in rows:
            note_id = int(row.id)
            b = self._bucket(note_id)
            self._s.execute(
                "DELETE FROM tbl_notes_by_news WHERE news_id = %s AND id = %s",
                (news_id, note_id),
            )
            self._s.execute(
                "DELETE FROM tbl_note_by_id WHERE id_bucket = %s AND id = %s",
                (b, note_id),
            )
