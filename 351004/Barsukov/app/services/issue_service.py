import json
from sqlalchemy.orm import Session
from repository import IssueRepo
from schemas.issue import IssueRequestTo, IssueResponseTo
from redis_config import redis_client

class IssueService:
    def __init__(self):
        self.repo = IssueRepo()
        self.cache_prefix = "issue:"

    def create(self, db: Session, dto: IssueRequestTo):
        data = dto.model_dump(exclude_none=True)
        if "authorId" in data: data["author_id"] = data.pop("authorId")
        if "stickerIds" in data: data.pop("stickerIds")
        return self.repo.create(db, data)

    def get_all(self, db: Session, skip=0, limit=10):
        return self.repo.get_all(db, skip=skip, limit=limit)

    async def get_by_id(self, db: Session, id: int):
        cache_key = f"{self.cache_prefix}{id}"
        cached = await redis_client.get(cache_key)
        if cached:
            return IssueResponseTo(**json.loads(cached))

        i = self.repo.get_by_id(db, id)
        if i:
            res = IssueResponseTo(
                id=i.id, authorId=i.author_id, title=i.title,
                content=i.content, created=str(i.created), modified=str(i.modified)
            )
            await redis_client.set(cache_key, res.model_dump_json(), ex=3600)
            return res
        return None

    async def update(self, db: Session, id: int, dto: IssueRequestTo):
        data = dto.model_dump(exclude_none=True)
        if "authorId" in data: data["author_id"] = data.pop("authorId")
        res = self.repo.update(db, id, data)
        if res:
            await redis_client.delete(f"{self.cache_prefix}{id}")
        return res

    async def delete(self, db: Session, id: int):
        success = self.repo.delete(db, id)
        if success:
            await redis_client.delete(f"{self.cache_prefix}{id}")
        return success