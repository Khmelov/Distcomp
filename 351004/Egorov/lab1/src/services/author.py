from typing import List

from src.core.constants import ErrorStatus
from src.core.errors import HttpNotFoundError, TopicErrorMessage
from src.domain.models.author import Author
from src.domain.repositories.interfaces.base import Repository
from src.schemas.author import AuthorResponseTo, AuthorRequestTo


class AuthorService:
    def __init__(self, repo: Repository[Author]) -> None:
        self._repo = repo

    def get_one(self, author_id: int) -> AuthorResponseTo:
        try:
            author = self._repo.get_one(author_id)
        except KeyError:
            raise HttpNotFoundError(TopicErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return AuthorResponseTo.model_validate(author)

    def get_all(self) -> List[AuthorResponseTo]:
        authors = self._repo.get_all()
        return [AuthorResponseTo.model_validate(a) for a in authors]

    def create(self, dto: AuthorRequestTo) -> AuthorResponseTo:
        author = Author(
            id=0,
            login=dto.login,
            password=dto.password,
            lastname=dto.lastname,
            firstname=dto.firstname,
        )
        created_author = self._repo.create(author)
        return AuthorResponseTo.model_validate(created_author)

    def update(self, author_id: int, dto: AuthorRequestTo) -> AuthorResponseTo:
        author = Author(
            id=author_id,
            login=dto.login,
            password=dto.password,
            lastname=dto.lastname,
            firstname=dto.firstname,
        )
        try:
            updated_author = self._repo.update(author)
        except KeyError:
            raise HttpNotFoundError(TopicErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return AuthorResponseTo.model_validate(updated_author)

    def delete(self, author_id: int) -> None:
        try:
            self._repo.delete(author_id)
        except KeyError:
            raise HttpNotFoundError(TopicErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)