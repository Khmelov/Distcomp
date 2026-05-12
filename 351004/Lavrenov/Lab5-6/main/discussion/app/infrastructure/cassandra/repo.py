from __future__ import annotations

import time
from datetime import datetime, timezone
from threading import Lock
from typing import Any, Dict, List, Optional

from cassandra.cluster import Session
from cassandra.query import BatchStatement

from discussion.app.schemas import NoteState


class CassandraNoteRepository:
    def __init__(self, session: Session, bucket_count: int = 16) -> None:
        self.session = session
        self.bucket_count = bucket_count

        self._id_lock = Lock()
        self._last_ms = 0
        self._seq = 0

        self._prepare_statements()

    def _prepare_statements(self) -> None:
        self.ps_insert_by_id = self.session.prepare(
            """
            INSERT INTO distcomp.tbl_note_by_id
            (id, article_id, content, state, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """
        )

        self.ps_select_by_id = self.session.prepare(
            """
            SELECT id, article_id, content, state, created_at, updated_at
            FROM distcomp.tbl_note_by_id
            WHERE id = ?
            """
        )

        self.ps_delete_by_id = self.session.prepare(
            """
            DELETE FROM distcomp.tbl_note_by_id
            WHERE id = ?
            """
        )

        self.ps_insert_by_article = self.session.prepare(
            """
            INSERT INTO distcomp.tbl_note_by_article
            (article_id, created_at, id, content, state, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """
        )

        self.ps_select_by_article = self.session.prepare(
            """
            SELECT article_id, created_at, id, content, state, updated_at
            FROM distcomp.tbl_note_by_article
            WHERE article_id = ?
            """
        )

        self.ps_delete_by_article = self.session.prepare(
            """
            DELETE FROM distcomp.tbl_note_by_article
            WHERE article_id = ? AND created_at = ? AND id = ?
            """
        )

        self.ps_insert_by_bucket = self.session.prepare(
            """
            INSERT INTO distcomp.tbl_note_by_bucket
            (bucket, created_at, id, article_id, content, state, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """
        )

        self.ps_select_by_bucket = self.session.prepare(
            """
            SELECT bucket, created_at, id, article_id, content, state, updated_at
            FROM distcomp.tbl_note_by_bucket
            WHERE bucket = ?
            """
        )

        self.ps_delete_by_bucket = self.session.prepare(
            """
            DELETE FROM distcomp.tbl_note_by_bucket
            WHERE bucket = ? AND created_at = ? AND id = ?
            """
        )

    def _now(self) -> datetime:
        return datetime.now(timezone.utc)

    def _generate_id(self) -> int:
        with self._id_lock:
            ms = int(time.time() * 1000)
            if ms == self._last_ms:
                self._seq = (self._seq + 1) % 1000
                if self._seq == 0:
                    while ms == self._last_ms:
                        ms = int(time.time() * 1000)
            else:
                self._seq = 0
            self._last_ms = ms
            return ms * 1000 + self._seq

    def _bucket_for(self, note_id: int) -> int:
        return note_id % self.bucket_count

    def _row_to_dict(self, row: Any) -> Dict[str, Any]:
        return {
            "id": row.id,
            "article_id": row.article_id,
            "content": row.content,
            "state": row.state,
            "created_at": row.created_at,
            "updated_at": row.updated_at,
        }

    def _public_dict(self, item: Dict[str, Any]) -> Dict[str, Any]:
        return {
            "id": item["id"],
            "articleId": item["article_id"],
            "content": item["content"],
            "state": item["state"],
            "createdAt": item["created_at"],
        }

    def get_by_id(self, note_id: int) -> Optional[Dict[str, Any]]:
        row = self.session.execute(self.ps_select_by_id, (note_id,)).one()
        if row is None:
            return None
        return self._public_dict(self._row_to_dict(row))

    def get_by_article_id(self, article_id: int) -> List[Dict[str, Any]]:
        rows = self.session.execute(self.ps_select_by_article, (article_id,))
        items = [self._public_dict(self._row_to_dict(row)) for row in rows]
        items.sort(key=lambda x: (x["createdAt"], x["id"]), reverse=True)
        return items

    def get_all(self, skip: int = 0, limit: int = 10) -> List[Dict[str, Any]]:
        items: List[Dict[str, Any]] = []
        for bucket in range(self.bucket_count):
            rows = self.session.execute(self.ps_select_by_bucket, (bucket,))
            for row in rows:
                items.append(self._public_dict(self._row_to_dict(row)))

        items.sort(key=lambda x: (x["createdAt"], x["id"]), reverse=True)

        if skip < 0:
            skip = 0
        if limit is None or limit <= 0:
            return items[skip:]
        return items[skip: skip + limit]

    def create(self, data: Dict[str, Any]) -> Dict[str, Any]:
        note_id = int(data.get("id") or self._generate_id())
        article_id = int(data["article_id"])
        content = data["content"]
        state = str(data.get("state") or NoteState.PENDING.value)
        created_at = data.get("created_at") or self._now()
        updated_at = data.get("updated_at") or created_at
        bucket = self._bucket_for(note_id)

        batch = BatchStatement()
        batch.add(self.ps_insert_by_id, (note_id, article_id, content, state, created_at, updated_at))
        batch.add(self.ps_insert_by_article, (article_id, created_at, note_id, content, state, updated_at))
        batch.add(self.ps_insert_by_bucket, (bucket, created_at, note_id, article_id, content, state, updated_at))
        self.session.execute(batch)

        return self._public_dict(
            {
                "id": note_id,
                "article_id": article_id,
                "content": content,
                "state": state,
                "created_at": created_at,
            }
        )

    def update(self, note_id: int, data: Dict[str, Any]) -> Optional[Dict[str, Any]]:
        existing_row = self.session.execute(self.ps_select_by_id, (note_id,)).one()
        if existing_row is None:
            return None

        existing = self._row_to_dict(existing_row)

        new_article_id = int(data.get("article_id", existing["article_id"]))
        new_content = data.get("content", existing["content"])
        new_state = str(data.get("state", existing["state"]))
        created_at = existing["created_at"]
        updated_at = self._now()
        bucket = self._bucket_for(note_id)

        batch = BatchStatement()

        if new_article_id != existing["article_id"]:
            batch.add(self.ps_delete_by_article, (existing["article_id"], existing["created_at"], note_id))

        batch.add(self.ps_insert_by_id, (note_id, new_article_id, new_content, new_state, created_at, updated_at))
        batch.add(self.ps_insert_by_article, (new_article_id, created_at, note_id, new_content, new_state, updated_at))
        batch.add(self.ps_insert_by_bucket, (bucket, created_at, note_id, new_article_id, new_content, new_state, updated_at))

        self.session.execute(batch)

        return self._public_dict(
            {
                "id": note_id,
                "article_id": new_article_id,
                "content": new_content,
                "state": new_state,
                "created_at": created_at,
            }
        )

    def delete(self, note_id: int) -> bool:
        row = self.session.execute(self.ps_select_by_id, (note_id,)).one()
        if row is None:
            return False

        existing = self._row_to_dict(row)
        bucket = self._bucket_for(note_id)

        batch = BatchStatement()
        batch.add(self.ps_delete_by_id, (note_id,))
        batch.add(self.ps_delete_by_article, (existing["article_id"], existing["created_at"], note_id))
        batch.add(self.ps_delete_by_bucket, (bucket, existing["created_at"], note_id))
        self.session.execute(batch)
        return True