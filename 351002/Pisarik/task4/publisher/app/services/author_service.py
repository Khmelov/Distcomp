from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dtos.author_request import AuthorRequestTo
from app.dtos.author_response import AuthorResponseTo
from app.db.orm import AuthorOrm
from app.repositories.sqlalchemy_repository import PageParams, SqlAlchemyRepository

class AuthorService:
    def __init__(self, db: Session) -> None:
        self._repo = SqlAlchemyRepository[AuthorOrm](db, AuthorOrm)
        self._db = db

    def create_author(self, dto: AuthorRequestTo) -> AuthorResponseTo:
        created = self._repo.create(
            AuthorOrm(login=dto.login, password=dto.password, firstname=dto.firstname, lastname=dto.lastname)
        )
        return AuthorResponseTo(id=created.id, login=created.login, firstname=created.firstname, lastname=created.lastname)

    def get_author(self, author_id: int) -> Optional[AuthorResponseTo]:
        entity = self._repo.get_by_id(author_id)
        if not entity:
            return None
        return AuthorResponseTo(id=entity.id, login=entity.login, firstname=entity.firstname, lastname=entity.lastname)

    def get_all_authors(
        self,
        page: PageParams,
        login: Optional[str] = None,
        firstname: Optional[str] = None,
        lastname: Optional[str] = None,
    ) -> List[AuthorResponseTo]:
        stmt = select(AuthorOrm)
        if login:
            stmt = stmt.where(AuthorOrm.login.ilike(f"%{login}%"))
        if firstname:
            stmt = stmt.where(AuthorOrm.firstname.ilike(f"%{firstname}%"))
        if lastname:
            stmt = stmt.where(AuthorOrm.lastname.ilike(f"%{lastname}%"))
        items = self._repo.list(stmt, page)
        return [AuthorResponseTo(id=i.id, login=i.login, firstname=i.firstname, lastname=i.lastname) for i in items]

    def update_author(self, author_id: int, dto: AuthorRequestTo) -> Optional[AuthorResponseTo]:
        existing = self._repo.get_by_id(author_id)
        if not existing:
            return None
        existing.login = dto.login
        existing.password = dto.password
        existing.firstname = dto.firstname
        existing.lastname = dto.lastname
        self._db.commit()
        self._db.refresh(existing)
        return AuthorResponseTo(id=existing.id, login=existing.login, firstname=existing.firstname, lastname=existing.lastname)

    def delete_author(self, author_id: int) -> bool:
        return self._repo.delete(author_id)
