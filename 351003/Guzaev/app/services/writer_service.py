from app.repositories.base_repository import InMemoryRepository
from app.models.writer import Writer
from app.dtos.writer_dto import WriterRequestTo, WriterResponseTo
from app.errors import AppError
from typing import List

writer_repository = InMemoryRepository[Writer]()


class WriterService:
    def create(self, dto: WriterRequestTo) -> WriterResponseTo:
        # Проверка уникальности логина
        existing = writer_repository.find_by_condition(lambda w: w.login == dto.login)
        if existing:
            raise AppError(status_code=403, message="Login already exists", error_code=40301)

        entity = Writer(
            login=dto.login,
            password=dto.password,
            firstname=dto.firstname,
            lastname=dto.lastname
        )
        saved_entity = writer_repository.create(entity)

        # ИСПРАВЛЕНИЕ: Преобразуем dataclass в dict для Pydantic
        return WriterResponseTo(
            id=saved_entity.id,
            login=saved_entity.login,
            firstname=saved_entity.firstname,
            lastname=saved_entity.lastname
        )

    def get_all(self) -> List[WriterResponseTo]:
        entities = writer_repository.get_all()
        return [
            WriterResponseTo(
                id=e.id,
                login=e.login,
                firstname=e.firstname,
                lastname=e.lastname
            )
            for e in entities
        ]

    def get_by_id(self, id: int) -> WriterResponseTo:
        entity = writer_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Writer not found", error_code=40401)

        return WriterResponseTo(
            id=entity.id,
            login=entity.login,
            firstname=entity.firstname,
            lastname=entity.lastname
        )

    def update(self, id: int, dto: WriterRequestTo) -> WriterResponseTo:
        entity = writer_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Writer not found", error_code=40402)

        # Проверка уникальности логина (кроме себя)
        existing = writer_repository.find_by_condition(
            lambda w: w.login == dto.login and w.id != id
        )
        if existing:
            raise AppError(status_code=403, message="Login already exists", error_code=40302)

        updated_entity = Writer(
            id=id,
            login=dto.login,
            password=dto.password,
            firstname=dto.firstname,
            lastname=dto.lastname
        )
        writer_repository.update(id, updated_entity)

        return WriterResponseTo(
            id=updated_entity.id,
            login=updated_entity.login,
            firstname=updated_entity.firstname,
            lastname=updated_entity.lastname
        )

    def delete(self, id: int) -> None:
        entity = writer_repository.get_by_id(id)
        if not entity:
            raise AppError(status_code=404, message="Writer not found", error_code=40403)
        writer_repository.delete(id)
