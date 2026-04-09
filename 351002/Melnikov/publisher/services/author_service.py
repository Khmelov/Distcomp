from ..common.errors import ConflictError, NotFoundError
from ..common.validation import validate_length
from ..dto.author_dto import AuthorRequestTo, AuthorResponseTo
from ..models.author import Author
from ..repositories.registry import author_repository


class AuthorService:
    def create(self, dto: AuthorRequestTo) -> AuthorResponseTo:
        login = validate_length("login", dto.login, 2, 64, 1)
        password = validate_length("password", dto.password, 8, 128, 2)
        firstname = validate_length("firstname", dto.firstname, 2, 64, 3)
        lastname = validate_length("lastname", dto.lastname, 2, 64, 4)

        existing = [a for a in author_repository.get_all() if a.login == login]
        if existing:
            raise ConflictError("Author with this login already exists", 1)

        author = Author(
            id=0,
            login=login,
            password=password,
            firstname=firstname,
            lastname=lastname
        )
        created = author_repository.create(author)
        return self._to_response(created)

    def get_all(self) -> list[AuthorResponseTo]:
        return [self._to_response(author) for author in author_repository.get_all()]

    def get_by_id(self, author_id: int) -> AuthorResponseTo:
        author = author_repository.get_by_id(author_id)
        if not author:
            raise NotFoundError("Author not found", 1)
        return self._to_response(author)

    def update(self, author_id: int, dto: AuthorRequestTo) -> AuthorResponseTo:
        author = author_repository.get_by_id(author_id)
        if not author:
            raise NotFoundError("Author not found", 1)

        login = validate_length("login", dto.login, 2, 64, 1)
        password = validate_length("password", dto.password, 8, 128, 2)
        firstname = validate_length("firstname", dto.firstname, 2, 64, 3)
        lastname = validate_length("lastname", dto.lastname, 2, 64, 4)

        for item in author_repository.get_all():
            if item.login == login and item.id != author_id:
                raise ConflictError("Author with this login already exists", 1)

        author.login = login
        author.password = password
        author.firstname = firstname
        author.lastname = lastname

        updated = author_repository.update(author)
        return self._to_response(updated)

    def delete(self, author_id: int) -> None:
        deleted = author_repository.delete(author_id)
        if not deleted:
            raise NotFoundError("Author not found", 1)

    @staticmethod
    def _to_response(author: Author) -> AuthorResponseTo:
        return AuthorResponseTo(
            id=author.id,
            login=author.login,
            password=author.password,
            firstname=author.firstname,
            lastname=author.lastname
        )