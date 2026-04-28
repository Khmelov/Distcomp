import json
from sqlalchemy.orm import Session
from repository import AuthorRepo
from schemas.author import AuthorRequestTo, AuthorResponseTo
from redis_config import redis_client


class AuthorService:
    def __init__(self):
        self.repo = AuthorRepo()
        self.cache_prefix = "author:"
        self.ttl = 3600  # Время жизни кеша - 1 час

    def create(self, db: Session, dto: AuthorRequestTo):
        return self.repo.create(db, dto.model_dump(exclude_none=True))

    def get_all(self, db: Session, skip=0, limit=10, sort="id", name=None):
        return self.repo.get_all(db, skip=skip, limit=limit, sort_by=sort, firstname=name)

    async def get_by_id(self, db: Session, id: int):
        cache_key = f"{self.cache_prefix}{id}"

        # 1. Пытаемся получить из Redis
        cached = await redis_client.get(cache_key)
        if cached:
            return AuthorResponseTo(**json.loads(cached))

        # 2. Если нет в кеше - идем в Postgres
        author = self.repo.get_by_id(db, id)
        if author:
            res = AuthorResponseTo(
                id=author.id,
                login=author.login,
                firstname=author.firstname,
                lastname=author.lastname
            )
            # 3. Сохраняем в кеш
            await redis_client.set(cache_key, res.model_dump_json(), ex=self.ttl)
            return res
        return None

    async def update(self, db: Session, id: int, dto: AuthorRequestTo):
        res = self.repo.update(db, id, dto.model_dump(exclude_none=True))
        if res:
            # Инвалидация кеша
            await redis_client.delete(f"{self.cache_prefix}{id}")
        return res

    async def delete(self, db: Session, id: int):
        success = self.repo.delete(db, id)
        if success:
            # Инвалидация кеша
            await redis_client.delete(f"{self.cache_prefix}{id}")
        return success