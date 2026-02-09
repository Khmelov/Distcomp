from app.repository.memory import author_repo
from app.schemas.author import AuthorRequestTo, AuthorResponseTo
from app.core.exceptions import AppException

class AuthorService:
    def create(self, dto: AuthorRequestTo) -> AuthorResponseTo:
        return AuthorResponseTo(**author_repo.save(dto.model_dump()))

    def get_all(self):
        return [AuthorResponseTo(**a) for a in author_repo.find_all()]

    def get_by_id(self, id: int):
        res = author_repo.find_by_id(id)
        if not res: raise AppException(404, "Author not found", 1)
        return AuthorResponseTo(**res)

    def update(self, id: int, dto: AuthorRequestTo) -> AuthorResponseTo:
        data = dto.model_dump()
        data["id"] = id
        saved = author_repo.save(data)
        return AuthorResponseTo(**saved)

    def delete(self, id: int):
        if not author_repo.delete(id):
            raise AppException(404, "Author not found", 3)