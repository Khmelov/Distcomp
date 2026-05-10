from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.repository.db import author_repo
from app.models.author import Author
from app.schemas.author import AuthorRequestTo, AuthorResponseTo
from app.core.exceptions import AppException
from app.core.security import get_password_hash
from app.core.redis import get_cache, set_cache, delete_cache

class AuthorService:
    async def create(self, session: AsyncSession, dto: AuthorRequestTo) -> AuthorResponseTo:
        res = await session.execute(select(Author).where(Author.login == dto.login))
        if res.scalar_one_or_none():
            raise AppException(403, "Login already exists", 17)

        data = dto.model_dump()
        
        data.pop("id", None)
        
        data["password"] = get_password_hash(data["password"])
        
        if not data.get("role"):
            data["role"] = "CUSTOMER"

        try:
            author = await author_repo.create(session, data)
        except Exception:
            await session.rollback()
            base_data = {
                "login": data["login"],
                "password": data["password"],
                "firstname": data["firstname"],
                "lastname": data["lastname"]
            }
            author = await author_repo.create(session, base_data)

        return AuthorResponseTo.model_validate(author)

    async def update(self, session: AsyncSession, id: int, dto: AuthorRequestTo) -> AuthorResponseTo:
        data = dto.model_dump(exclude={'id'})
        
        if data.get("password"):
            data["password"] = get_password_hash(data["password"])
        else:
            data.pop("password", None)
            
        updated = await author_repo.update(session, id, data)
        if not updated: raise AppException(404, "Author not found", 2)
        
        await delete_cache(f"author:{id}")
        return AuthorResponseTo.model_validate(updated, from_attributes=True)

    async def get_all(self, session: AsyncSession, page: int = 1, size: int = 100):
        authors = await author_repo.get_all(session, limit=size, offset=(page - 1) * size)
        return [AuthorResponseTo.model_validate(a) for a in authors]
    
    async def get_by_id(self, session: AsyncSession, id: int):
        cache_key = f"author:{id}"
        try:
            cached = await get_cache(cache_key)
            if cached:
                return AuthorResponseTo.model_validate(cached)
        except Exception:
            await delete_cache(cache_key)

        res = await author_repo.get_by_id(session, id)
        if not res: 
            raise AppException(404, "Author not found", 1)
        
        resp = AuthorResponseTo.model_validate(res)
        
        try:
            await set_cache(cache_key, resp.model_dump())
        except Exception:
            pass
            
        return resp

    async def delete(self, session: AsyncSession, id: int):
        try:
            if not await author_repo.delete(session, id):
                raise AppException(404, "Author not found", 3)
            await delete_cache(f"author:{id}")
        except AppException:
            raise
        except Exception as e:
            await session.rollback()
            raise AppException(400, f"Delete failed: {str(e)}", 30)