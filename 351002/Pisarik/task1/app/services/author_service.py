from typing import List, Optional
from app.models.author import Author
from app.repositories.in_memory_repository import InMemoryRepository
from app.dtos.author_request import AuthorRequestTo
from app.dtos.author_response import AuthorResponseTo

class AuthorService:
    def __init__(self, repo: InMemoryRepository[Author]) -> None:
        self._repo = repo

    def create_author(self, dto: AuthorRequestTo) -> AuthorResponseTo:
        entity = Author(
            id=self._repo.next_id(),
            login=dto.login,
            password=dto.password,
            firstname=dto.firstname,
            lastname=dto.lastname,
        )
        created = self._repo.create(entity)
        return AuthorResponseTo(id=created.id, login=created.login, firstname=created.firstname, lastname=created.lastname)

    def get_author(self, author_id: int) -> Optional[AuthorResponseTo]:
        entity = self._repo.get_by_id(author_id)
        if not entity:
            return None
        return AuthorResponseTo(id=entity.id, login=entity.login, firstname=entity.firstname, lastname=entity.lastname)

    def get_all_authors(self) -> List[AuthorResponseTo]:
        return [AuthorResponseTo(id=i.id, login=i.login, firstname=i.firstname, lastname=i.lastname) for i in self._repo.get_all()]

    def update_author(self, author_id: int, dto: AuthorRequestTo) -> Optional[AuthorResponseTo]:
        existing = self._repo.get_by_id(author_id)
        if not existing:
            return None
        updated_entity = Author(id=author_id, login=dto.login, password=dto.password, firstname=dto.firstname, lastname=dto.lastname)
        updated = self._repo.update(author_id, updated_entity)
        if not updated:
            return None
        return AuthorResponseTo(id=updated.id, login=updated.login, firstname=updated.firstname, lastname=updated.lastname)

    def delete_author(self, author_id: int) -> bool:
        return self._repo.delete(author_id)
