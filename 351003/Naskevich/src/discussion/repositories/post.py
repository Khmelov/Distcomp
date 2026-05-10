import asyncio
from time import time_ns

from cassandra.cluster import Session

from src.models.post import Post
from src.models.post_state import PostState
from src.repositories.post import AbstractPostRepository


def _state_from_row(state_val: str | None) -> PostState:
    if not state_val:
        return PostState.APPROVE
    try:
        return PostState(state_val)
    except ValueError:
        return PostState.APPROVE


class CassandraPostRepository(AbstractPostRepository):
    def __init__(self, session: Session) -> None:
        self._session = session

    async def get_by_id(self, entity_id: int) -> Post | None:
        def _load() -> Post | None:
            row = self._session.execute(
                "SELECT tweet_id FROM posts_by_post_id WHERE id = %s",
                (entity_id,),
            ).one()
            if row is None:
                return None
            tweet_id = row.tweet_id
            row2 = self._session.execute(
                "SELECT content, state FROM posts WHERE tweet_id = %s AND id = %s",
                (tweet_id, entity_id),
            ).one()
            if row2 is None:
                return None
            p = Post(
                tweet_id=tweet_id,
                content=row2.content,
                state=_state_from_row(row2.state),
            )
            p.id = entity_id
            return p

        return await asyncio.to_thread(_load)

    async def create(self, entity: Post) -> Post:
        new_id = entity.id if entity.id > 0 else time_ns()

        def _ins() -> Post:
            self._session.execute(
                "INSERT INTO posts (tweet_id, id, content, state) VALUES (%s, %s, %s, %s)",
                (entity.tweet_id, new_id, entity.content, entity.state.value),
            )
            self._session.execute(
                "INSERT INTO posts_by_post_id (id, tweet_id) VALUES (%s, %s)",
                (new_id, entity.tweet_id),
            )
            entity.id = new_id
            return entity

        return await asyncio.to_thread(_ins)

    async def update(self, entity: Post) -> Post | None:
        def _upd() -> Post | None:
            row = self._session.execute(
                "SELECT tweet_id FROM posts_by_post_id WHERE id = %s",
                (entity.id,),
            ).one()
            if row is None:
                return None
            old_tid = row.tweet_id
            if old_tid == entity.tweet_id:
                self._session.execute(
                    "UPDATE posts SET content = %s, state = %s WHERE tweet_id = %s AND id = %s",
                    (entity.content, entity.state.value, entity.tweet_id, entity.id),
                )
            else:
                self._session.execute(
                    "DELETE FROM posts WHERE tweet_id = %s AND id = %s",
                    (old_tid, entity.id),
                )
                self._session.execute(
                    "INSERT INTO posts (tweet_id, id, content, state) VALUES (%s, %s, %s, %s)",
                    (entity.tweet_id, entity.id, entity.content, entity.state.value),
                )
                self._session.execute(
                    "UPDATE posts_by_post_id SET tweet_id = %s WHERE id = %s",
                    (entity.tweet_id, entity.id),
                )
            return entity

        return await asyncio.to_thread(_upd)

    async def delete(self, entity_id: int) -> bool:
        def _del() -> bool:
            row = self._session.execute(
                "SELECT tweet_id FROM posts_by_post_id WHERE id = %s",
                (entity_id,),
            ).one()
            if row is None:
                return False
            tid = row.tweet_id
            self._session.execute(
                "DELETE FROM posts WHERE tweet_id = %s AND id = %s",
                (tid, entity_id),
            )
            self._session.execute(
                "DELETE FROM posts_by_post_id WHERE id = %s",
                (entity_id,),
            )
            return True

        return await asyncio.to_thread(_del)

    async def get_by_tweet_id(self, tweet_id: int) -> list[Post]:
        def _list() -> list[Post]:
            rows = self._session.execute(
                "SELECT id, content, state FROM posts WHERE tweet_id = %s",
                (tweet_id,),
            )
            out: list[Post] = []
            for r in rows:
                p = Post(
                    tweet_id=tweet_id,
                    content=r.content,
                    state=_state_from_row(r.state),
                )
                p.id = r.id
                out.append(p)
            return out

        return await asyncio.to_thread(_list)

    async def get_all(self) -> list[Post]:
        def _all() -> list[Post]:
            rows = self._session.execute(
                "SELECT tweet_id, id, content, state FROM posts ALLOW FILTERING"
            )
            out: list[Post] = []
            for r in rows:
                p = Post(
                    tweet_id=r.tweet_id,
                    content=r.content,
                    state=_state_from_row(r.state),
                )
                p.id = r.id
                out.append(p)
            return out

        return await asyncio.to_thread(_all)
