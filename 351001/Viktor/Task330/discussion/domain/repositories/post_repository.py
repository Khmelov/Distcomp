from cassandra.query import SimpleStatement, PreparedStatement
from typing import List, Optional
import uuid

class CassandraPostRepository:
    def __init__(self, session):
        self.session = session

    async def get_one(self, tweet_id: int, post_id: int) -> Optional[dict]:
        stmt = SimpleStatement("SELECT * FROM tbl_post WHERE tweet_id = %s AND id = %s")
        rows = self.session.execute(stmt, (tweet_id, post_id))
        row = rows.one()
        return {"id": row.id, "tweet_id": row.tweet_id, "content": row.content} if row else None

    async def get_all(self, tweet_id: int, page: int = 1, size: int = 20) -> List[dict]:
        # Для простоты пропустим пагинацию (в Cassandra пагинация сложнее)
        stmt = SimpleStatement("SELECT * FROM tbl_post WHERE tweet_id = %s")
        rows = self.session.execute(stmt, (tweet_id,))
        posts = [{"id": row.id, "tweet_id": row.tweet_id, "content": row.content} for row in rows]
        start = (page - 1) * size
        return posts[start:start+size]

    async def create(self, tweet_id: int, content: str) -> dict:
        # Генерируем уникальный id (можно использовать timeuuid или последовательность)
        new_id = uuid.uuid1().int & 0xFFFFFFFFFFFFFFFF  # 64 бита
        stmt = SimpleStatement("INSERT INTO tbl_post (tweet_id, id, content) VALUES (%s, %s, %s)")
        self.session.execute(stmt, (tweet_id, new_id, content))
        return {"id": new_id, "tweet_id": tweet_id, "content": content}

    async def update(self, tweet_id: int, post_id: int, content: str) -> Optional[dict]:
        stmt = SimpleStatement("UPDATE tbl_post SET content = %s WHERE tweet_id = %s AND id = %s")
        self.session.execute(stmt, (content, tweet_id, post_id))
        return {"id": post_id, "tweet_id": tweet_id, "content": content}

    async def delete(self, tweet_id: int, post_id: int) -> bool:
        stmt = SimpleStatement("DELETE FROM tbl_post WHERE tweet_id = %s AND id = %s")
        self.session.execute(stmt, (tweet_id, post_id))
        return True

    async def get_one_by_id(self, post_id: int) -> Optional[dict]:
        stmt = SimpleStatement("SELECT * FROM tbl_post WHERE id = %s")
        rows = self.session.execute(stmt, (post_id,))
        row = rows.one()
        if row:
            return {"id": row.id, "tweet_id": row.tweet_id, "content": row.content}
        return None

    async def get_all_posts(self, page: int = 1, size: int = 20) -> List[dict]:
        stmt = SimpleStatement("SELECT * FROM tbl_post")
        rows = self.session.execute(stmt)
        posts = [{"id": row.id, "tweet_id": row.tweet_id, "content": row.content} for row in rows]
        start = (page - 1) * size
        return posts[start:start + size]