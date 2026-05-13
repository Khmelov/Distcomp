from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dtos.author_register_v2 import AuthorRegisterV2To
from app.dtos.author_request import AuthorRequestTo
from app.dtos.author_response import AuthorResponseTo
from app.db.orm import AuthorOrm
from app.redis_cache import cache_get_json, cache_set_json, entity_id_key, entity_list_key, invalidate_entity
from app.repositories.sqlalchemy_repository import PageParams, SqlAlchemyRepository
from app.security.passwords import hash_password, verify_password


class AuthorService:
    def __init__(self, db: Session) -> None:
        self._repo = SqlAlchemyRepository[AuthorOrm](db, AuthorOrm)
        self._db = db

    def _to_response(self, created: AuthorOrm) -> AuthorResponseTo:
        return AuthorResponseTo(
            id=created.id,
            login=created.login,
            firstname=created.firstname,
            lastname=created.lastname,
            role=created.role,
        )

    def get_by_login(self, login: str) -> Optional[AuthorOrm]:
        return self._db.execute(select(AuthorOrm).where(AuthorOrm.login == login)).scalar_one_or_none()

    def authenticate(self, login: str, password: str) -> Optional[AuthorOrm]:
        row = self.get_by_login(login)
        if row is None:
            return None
        if not verify_password(password, row.password):
            return None
        return row

    def register_v2(self, dto: AuthorRegisterV2To) -> AuthorResponseTo:
        role = dto.role or "CUSTOMER"
        created = self._repo.create(
            AuthorOrm(
                login=dto.login,
                password=hash_password(dto.password),
                firstname=dto.firstname,
                lastname=dto.lastname,
                role=role,
            )
        )
        invalidate_entity("author")
        out = self._to_response(created)
        cache_set_json(entity_id_key("author", out.id), out.model_dump(mode="json"))
        return out

    def create_author(self, dto: AuthorRequestTo) -> AuthorResponseTo:
        created = self._repo.create(
            AuthorOrm(
                login=dto.login,
                password=hash_password(dto.password),
                firstname=dto.firstname,
                lastname=dto.lastname,
                role="CUSTOMER",
            )
        )
        invalidate_entity("author")
        out = self._to_response(created)
        cache_set_json(entity_id_key("author", out.id), out.model_dump(mode="json"))
        return out

    def get_author(self, author_id: int) -> Optional[AuthorResponseTo]:
        k = entity_id_key("author", author_id)
        cached = cache_get_json(k)
        if isinstance(cached, dict):
            try:
                return AuthorResponseTo.model_validate(cached)
            except Exception:
                pass
        entity = self._repo.get_by_id(author_id)
        if not entity:
            return None
        out = self._to_response(entity)
        cache_set_json(k, out.model_dump(mode="json"))
        return out

    def get_all_authors(
        self,
        page: PageParams,
        login: Optional[str] = None,
        firstname: Optional[str] = None,
        lastname: Optional[str] = None,
    ) -> List[AuthorResponseTo]:
        params = {
            "page": page.page,
            "size": page.size,
            "sort": page.sort,
            "login": login or "",
            "firstname": firstname or "",
            "lastname": lastname or "",
        }
        lk = entity_list_key("author", params)
        cached = cache_get_json(lk)
        if isinstance(cached, list):
            try:
                return [AuthorResponseTo.model_validate(x) for x in cached]
            except Exception:
                pass
        stmt = select(AuthorOrm)
        if login:
            stmt = stmt.where(AuthorOrm.login.ilike(f"%{login}%"))
        if firstname:
            stmt = stmt.where(AuthorOrm.firstname.ilike(f"%{firstname}%"))
        if lastname:
            stmt = stmt.where(AuthorOrm.lastname.ilike(f"%{lastname}%"))
        items = self._repo.list(stmt, page)
        out = [self._to_response(i) for i in items]
        cache_set_json(lk, [x.model_dump(mode="json") for x in out])
        return out

    def update_author(self, author_id: int, dto: AuthorRequestTo) -> Optional[AuthorResponseTo]:
        existing = self._repo.get_by_id(author_id)
        if not existing:
            return None
        existing.login = dto.login
        existing.password = hash_password(dto.password)
        existing.firstname = dto.firstname
        existing.lastname = dto.lastname
        self._db.commit()
        self._db.refresh(existing)
        invalidate_entity("author", author_id)
        out = self._to_response(existing)
        cache_set_json(entity_id_key("author", out.id), out.model_dump(mode="json"))
        return out

    def delete_author(self, author_id: int) -> bool:
        ok = self._repo.delete(author_id)
        if ok:
            invalidate_entity("author", author_id)
        return ok
