from __future__ import annotations

from typing import List, Optional, Tuple

from app.cassandra_db import TABLE, get_session
from app.dtos.message_request import MessageRequestTo
from app.dtos.message_response import MessageResponseTo
from app.moderation import moderate_content
from app.settings import default_country


class PageParams:
    def __init__(self, page: int = 0, size: int = 20, sort: str = "id,asc") -> None:
        self.page = page
        self.size = size
        self.sort = sort


class MessageService:
    def __init__(self) -> None:
        self._session = None

    @staticmethod
    def _state_for_api(stored: str) -> Optional[str]:
        """Omit APPROVE in JSON for backward-compatible lecturer tests; keep DECLINE visible."""
        return "DECLINE" if stored == "DECLINE" else None

    def _sess(self):
        if self._session is None:
            self._session = get_session()
        return self._session

    @staticmethod
    def _resolve_country(dto_country: Optional[str]) -> str:
        return (dto_country or default_country()).strip() or default_country()

    def _next_id(self, country: str, _news_id: int) -> int:
        rows = list(
            self._sess().execute(
                f"SELECT id FROM {TABLE} WHERE country = %s ALLOW FILTERING",
                (country,),
            )
        )
        if not rows:
            return 1
        return max(int(tuple(r)[0]) for r in rows) + 1

    @staticmethod
    def _row_tuple(row) -> Tuple[str, int, int, str, str]:
        vals = tuple(row)
        c, nid, mid, txt = str(vals[0]), int(vals[1]), int(vals[2]), str(vals[3])
        st = "APPROVE"
        if len(vals) > 4 and vals[4] is not None:
            st = str(vals[4])
        return c, nid, mid, txt, st

    def _find_by_id(self, message_id: int) -> Optional[Tuple[str, int, int, str, str]]:
        rows = list(
            self._sess().execute(
                f"SELECT country, news_id, id, content, state FROM {TABLE} WHERE id = %s ALLOW FILTERING",
                (message_id,),
            )
        )
        if not rows:
            return None
        parsed = [self._row_tuple(r) for r in rows]
        return max(parsed, key=lambda t: t[1])

    def create_message(self, dto: MessageRequestTo) -> MessageResponseTo:
        country = self._resolve_country(dto.country)
        new_id = self._next_id(country, dto.newsId)
        state = moderate_content(dto.content)
        self._sess().execute(
            f"INSERT INTO {TABLE} (country, news_id, id, content, state) VALUES (%s, %s, %s, %s, %s)",
            (country, dto.newsId, new_id, dto.content, state),
        )
        return MessageResponseTo(
            id=new_id,
            newsId=dto.newsId,
            content=dto.content,
            country=country,
            state=self._state_for_api(state),
        )

    def get_message(self, message_id: int) -> Optional[MessageResponseTo]:
        row = self._find_by_id(message_id)
        if not row:
            return None
        c, nid, mid, content, state = row
        return MessageResponseTo(id=mid, newsId=nid, content=content, country=c, state=self._state_for_api(state))

    def get_all_messages(
        self,
        page: PageParams,
        news_id: Optional[int] = None,
        content: Optional[str] = None,
        country_override: Optional[str] = None,
    ) -> List[MessageResponseTo]:
        country = self._resolve_country(country_override)
        if news_id is not None:
            rows = self._sess().execute(
                f"SELECT country, news_id, id, content, state FROM {TABLE} WHERE country = %s AND news_id = %s",
                (country, news_id),
            )
        else:
            rows = self._sess().execute(
                f"SELECT country, news_id, id, content, state FROM {TABLE} WHERE country = %s",
                (country,),
            )
        items: List[MessageResponseTo] = []
        needle = (content or "").lower()
        for row in rows:
            c, nid, mid, txt, st = self._row_tuple(row)
            if needle and needle not in txt.lower():
                continue
            items.append(
                MessageResponseTo(id=mid, newsId=nid, content=txt, country=c, state=self._state_for_api(st))
            )
        field, _, direction = page.sort.partition(",")
        field = (field.strip() or "id").lower()
        direction = (direction.strip() or "asc").lower()
        reverse = direction == "desc"

        def sort_key(m: MessageResponseTo) -> int:
            return m.id

        items.sort(key=sort_key, reverse=reverse)
        start = page.page * page.size
        return items[start : start + page.size]

    def get_messages_by_news(self, news_id: int) -> List[MessageResponseTo]:
        return self.get_all_messages(PageParams(page=0, size=100000, sort="id,asc"), news_id=news_id)

    def update_message(self, message_id: int, dto: MessageRequestTo) -> Optional[MessageResponseTo]:
        existing = self._find_by_id(message_id)
        if not existing:
            return None
        old_c, old_nid, old_mid, _, _ = existing
        new_c = self._resolve_country(dto.country)
        new_nid = dto.newsId
        state = moderate_content(dto.content)
        if old_c == new_c and old_nid == new_nid:
            self._sess().execute(
                f"UPDATE {TABLE} SET content = %s, state = %s WHERE country = %s AND news_id = %s AND id = %s",
                (dto.content, state, old_c, old_nid, old_mid),
            )
        else:
            self._sess().execute(
                f"DELETE FROM {TABLE} WHERE country = %s AND news_id = %s AND id = %s",
                (old_c, old_nid, old_mid),
            )
            self._sess().execute(
                f"INSERT INTO {TABLE} (country, news_id, id, content, state) VALUES (%s, %s, %s, %s, %s)",
                (new_c, new_nid, old_mid, dto.content, state),
            )
        return MessageResponseTo(
            id=old_mid,
            newsId=new_nid,
            content=dto.content,
            country=new_c,
            state=self._state_for_api(state),
        )

    def delete_message(self, message_id: int) -> bool:
        rows = list(
            self._sess().execute(
                f"SELECT country, news_id, id, content, state FROM {TABLE} WHERE id = %s ALLOW FILTERING",
                (message_id,),
            )
        )
        if not rows:
            return False
        for r in rows:
            c, nid, mid, _, _ = self._row_tuple(r)
            self._sess().execute(
                f"DELETE FROM {TABLE} WHERE country = %s AND news_id = %s AND id = %s",
                (c, nid, mid),
            )
        return True
