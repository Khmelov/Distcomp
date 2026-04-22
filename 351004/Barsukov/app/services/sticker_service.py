import json
from sqlalchemy.orm import Session
from repository import StickerRepo
from schemas.sticker import StickerRequestTo, StickerResponseTo
from redis_config import redis_client

class StickerService:
    def __init__(self):
        self.repo = StickerRepo()
        self.cache_prefix = "sticker:"

    def create(self, db: Session, dto: StickerRequestTo):
        return self.repo.create(db, dto.model_dump(exclude_none=True))

    def get_all(self, db: Session):
        return self.repo.get_all(db)

    async def get_by_id(self, db: Session, id: int):
        cache_key = f"{self.cache_prefix}{id}"
        cached = await redis_client.get(cache_key)
        if cached:
            return StickerResponseTo(**json.loads(cached))

        res = self.repo.get_by_id(db, id)
        if res:
            dto = StickerResponseTo(id=res.id, name=res.name)
            await redis_client.set(cache_key, dto.model_dump_json(), ex=3600)
            return dto
        return None

    async def update(self, db: Session, id: int, dto: StickerRequestTo):
        res = self.repo.update(db, id, dto.model_dump(exclude_none=True))
        if res:
            await redis_client.delete(f"{self.cache_prefix}{id}")
        return res

    async def delete(self, db: Session, id: int):
        success = self.repo.delete(db, id)
        if success:
            await redis_client.delete(f"{self.cache_prefix}{id}")
        return success