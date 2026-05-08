from repositories.base_repository import BaseRepository
from models.models import Writer
from dtos.writer_dto import WriterRequestTo, WriterResponseTo
from database import SessionLocal
from errors import AppError
from typing import List
from sqlalchemy.exc import IntegrityError

writer_repo = BaseRepository[Writer](Writer)

class WriterService:
    def create(self, dto: WriterRequestTo) -> WriterResponseTo:
        with SessionLocal() as db:
            entity = Writer(
                login=dto.login,
                password=dto.password,
                firstname=dto.firstname,
                lastname=dto.lastname
            )
            try:
                saved = writer_repo.create(db, entity)
            except IntegrityError:
                db.rollback()
                # Конфликт уникального логина → 403, как ждёт тест
                raise AppError(status_code=403, message="Login already exists", error_code=40301)

            return WriterResponseTo(
                id=saved.id,
                login=saved.login,
                firstname=saved.firstname,
                lastname=saved.lastname
            )


    def get_all(self) -> List[WriterResponseTo]:
        with SessionLocal() as db:
            entities = writer_repo.get_all(db)
            return [
                WriterResponseTo(
                    id=e.id, login=e.login,
                    firstname=e.firstname, lastname=e.lastname
                ) for e in entities
            ]

    def get_by_id(self, id: int) -> WriterResponseTo:
        with SessionLocal() as db:
            entity = writer_repo.get_by_id(db, id)
            if not entity:
                raise AppError(status_code=404, message="Writer not found", error_code=40401)
            return WriterResponseTo(
                id=entity.id, login=entity.login,
                firstname=entity.firstname, lastname=entity.lastname
            )

    def update(self, id: int, dto: WriterRequestTo) -> WriterResponseTo:
        with SessionLocal() as db:
            entity = writer_repo.get_by_id(db, id)
            if not entity:
                raise AppError(status_code=404, message="Writer not found", error_code=40402)

            existing = db.query(Writer).filter(Writer.login == dto.login, Writer.id != id).first()
            if existing:
                raise AppError(status_code=403, message="Login already exists", error_code=40302)

            # Обновляем поля сущности
            entity.login = dto.login
            entity.password = dto.password
            entity.firstname = dto.firstname
            entity.lastname = dto.lastname

            db.commit()
            db.refresh(entity)
            return WriterResponseTo(
                id=entity.id, login=entity.login,
                firstname=entity.firstname, lastname=entity.lastname
            )

    def delete(self, id: int) -> None:
        with SessionLocal() as db:
            success = writer_repo.delete(db, id)
            if not success:
                raise AppError(status_code=404, message="Writer not found", error_code=40403)
