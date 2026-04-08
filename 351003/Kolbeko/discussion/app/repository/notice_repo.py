from typing import Dict, List, Optional

from discussion.app.core.cassandra import cassandra_session, notice_bucket


class NoticeRepository:
    async def create(self, data: Dict) -> Dict:
        s = cassandra_session()
        bucket = notice_bucket(int(data["id"]))
        s.execute(
            "INSERT INTO tbl_notice_by_id (bucket, id, tweet_id, content, state) VALUES (%s, %s, %s, %s, %s)",
            (bucket, int(data["id"]), int(data["tweet_id"]), data["content"], data["state"]),
        )
        s.execute(
            "INSERT INTO tbl_notice_by_tweet (tweet_id, id, content, state) VALUES (%s, %s, %s, %s)",
            (int(data["tweet_id"]), int(data["id"]), data["content"], data["state"]),
        )
        return {**data, "bucket": bucket}

    async def get_by_id(self, id: int) -> Optional[Dict]:
        s = cassandra_session()
        bucket = notice_bucket(int(id))
        row = s.execute(
            "SELECT id, tweet_id, content, state FROM tbl_notice_by_id WHERE bucket=%s AND id=%s",
            (bucket, int(id)),
        ).one()
        if not row:
            return None
        return {"id": row.id, "tweet_id": row.tweet_id, "content": row.content, "state": row.state}

    async def update(self, id: int, tweet_id: int, content: str) -> Optional[Dict]:
        existing = await self.get_by_id(id)
        if not existing:
            return None
        state = existing["state"]
        s = cassandra_session()
        bucket = notice_bucket(int(id))
        s.execute(
            "UPDATE tbl_notice_by_id SET tweet_id=%s, content=%s, state=%s WHERE bucket=%s AND id=%s",
            (int(tweet_id), content, state, bucket, int(id)),
        )
        # Ensure tweet-index table is consistent (delete old if tweet changed)
        if existing["tweet_id"] != tweet_id:
            s.execute("DELETE FROM tbl_notice_by_tweet WHERE tweet_id=%s AND id=%s", (int(existing["tweet_id"]), int(id)))
        s.execute(
            "INSERT INTO tbl_notice_by_tweet (tweet_id, id, content, state) VALUES (%s, %s, %s, %s)",
            (int(tweet_id), int(id), content, state),
        )
        return {"id": int(id), "tweet_id": int(tweet_id), "content": content, "state": state}

    async def delete(self, id: int) -> bool:
        existing = await self.get_by_id(id)
        if not existing:
            return False
        s = cassandra_session()
        bucket = notice_bucket(int(id))
        s.execute("DELETE FROM tbl_notice_by_id WHERE bucket=%s AND id=%s", (bucket, int(id)))
        s.execute("DELETE FROM tbl_notice_by_tweet WHERE tweet_id=%s AND id=%s", (int(existing["tweet_id"]), int(id)))
        return True

    async def get_all(self, page: int = 1, page_size: int = 10) -> List[Dict]:
        """
        Cassandra doesn't support efficient global pagination without a dedicated model.
        For lab purposes we do a best-effort scan across buckets.
        """
        s = cassandra_session()
        offset = (page - 1) * page_size
        collected: List[Dict] = []
        
        for bucket in range(16):
            rows = list(
                s.execute(
                    "SELECT id, tweet_id, content, state FROM tbl_notice_by_id WHERE bucket=%s",
                    (bucket,),
                )
            )
            for r in rows:
                collected.append({"id": r.id, "tweet_id": r.tweet_id, "content": r.content, "state": r.state})
        collected.sort(key=lambda x: x["id"], reverse=False)
        return collected[offset : offset + page_size]

