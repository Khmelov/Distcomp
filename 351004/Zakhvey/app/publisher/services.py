from datetime import datetime, timezone
from sqlalchemy.orm import Session
from models import User, Issue, Label
from schemas import (
    UserRequestTo, UserResponseTo,
    IssueRequestTo, ArticleResponseTo,
    LabelRequestTo, LabelResponseTo,
    CommentRequestTo, CommentResponseTo
)
from repositories import SQLAlchemyRepository
from exceptions import AppError
import httpx


# Адрес второго микросервиса
DISCUSSION_URL = "http://localhost:24130/api/v1.0/comments"


class UserService:
    def __init__(self, db: Session):
        self.repo = SQLAlchemyRepository(User, db)

    def create(self, dto: UserRequestTo) -> UserResponseTo:
        existing = self.repo.find_all(login=dto.login)
        if existing:
            # ТРЕБОВАНИЕ ТЕСТА: Ошибка 403
            raise AppError(403, 40301, "User with this login already exists")
        entity = User(**dto.model_dump())
        saved = self.repo.save(entity)
        return UserResponseTo.model_validate(saved)

    def get_all(self, skip: int = 0, limit: int = 100, sort_by: str = 'id') -> list[UserResponseTo]:
        users = self.repo.find_all(skip=skip, limit=limit, sort_by=sort_by)
        return [UserResponseTo.model_validate(u) for u in users]

    def get_by_id(self, user_id: int) -> UserResponseTo:
        entity = self.repo.find_by_id(user_id)
        if not entity:
            raise AppError(404, 40401, f"User with id {user_id} not found")
        return UserResponseTo.model_validate(entity)

    def update(self, user_id: int, dto: UserRequestTo) -> UserResponseTo:
        entity = self.repo.find_by_id(user_id)
        if not entity:
            raise AppError(404, 40401, f"User with id {user_id} not found")
        for key, value in dto.model_dump().items():
            setattr(entity, key, value)
        updated = self.repo.save(entity)
        return UserResponseTo.model_validate(updated)

    def delete(self, user_id: int):
        if not self.repo.delete(user_id):
            raise AppError(404, 40401, f"User with id {user_id} not found")


class IssueService:
    def __init__(self, db: Session):
        self.repo = SQLAlchemyRepository(Issue, db)
        self.user_repo = SQLAlchemyRepository(User, db)
        self.label_repo = SQLAlchemyRepository(Label, db)
        self.db = db

    def create(self, dto: IssueRequestTo) -> ArticleResponseTo:
        # 1. ТРЕБОВАНИЕ: Проверка на дубликат заголовка (Статус 403)
        if self.repo.find_all(title=dto.title):
            raise AppError(403, 40302, f"Issue with title '{dto.title}' already exists")

        if not self.user_repo.find_by_id(dto.userId):
            raise AppError(400, 40003, f"User with id {dto.userId} does not exist")

        now = datetime.now(timezone.utc)
        entity = Issue(
            user_id=dto.userId,
            title=dto.title,
            content=dto.content,
            created=now,
            modified=now
        )

        # Обработка меток
        if dto.labels:
            for label_name in dto.labels:
                existing_labels = self.label_repo.find_all(name=label_name)
                if existing_labels:
                    entity.labels.append(existing_labels[0])
                else:
                    new_label = Label(name=label_name)
                    # Используем db.add вместо repo.save, чтобы зафиксировать всё одной транзакцией в конце
                    self.db.add(new_label)
                    entity.labels.append(new_label)

        saved = self.repo.save(entity)
        return self._map_to_dto(saved)

    def get_all(self, skip: int = 0, limit: int = 100, sort_by: str = 'id') -> list[ArticleResponseTo]:
        issues = self.repo.find_all(skip=skip, limit=limit, sort_by=sort_by)
        return [self._map_to_dto(i) for i in issues]

    def get_by_id(self, issue_id: int) -> ArticleResponseTo:
        entity = self.repo.find_by_id(issue_id)
        if not entity:
            raise AppError(404, 40402, f"Issue with id {issue_id} not found")
        return self._map_to_dto(entity)

    def get_labels_for_issue(self, issue_id: int) -> list[LabelResponseTo]:
        entity = self.repo.find_by_id(issue_id)
        if not entity:
            raise AppError(404, 40402, f"Issue with id {issue_id} not found")
        return [LabelResponseTo.model_validate(l) for l in entity.labels]

    def update(self, issue_id: int, dto: IssueRequestTo) -> ArticleResponseTo:
        entity = self.repo.find_by_id(issue_id)
        if not entity:
            raise AppError(404, 40402, f"Issue with id {issue_id} not found")

        if not self.user_repo.find_by_id(dto.userId):
            raise AppError(400, 40003, f"User with id {dto.userId} does not exist")

        entity.user_id = dto.userId
        entity.title = dto.title
        entity.content = dto.content
        entity.modified = datetime.now(timezone.utc)

        entity.labels = []
        if dto.labels:
            for label_name in dto.labels:
                existing_labels = self.label_repo.find_all(name=label_name)
                if existing_labels:
                    entity.labels.append(existing_labels[0])
                else:
                    new_label = Label(name=label_name)
                    self.db.add(new_label)
                    entity.labels.append(new_label)

        updated = self.repo.save(entity)
        return self._map_to_dto(updated)

    def delete(self, issue_id: int):
        entity = self.repo.find_by_id(issue_id)
        if not entity:
            raise AppError(404, 40402, f"Issue with id {issue_id} not found")

        # Запоминаем метки этой статьи перед удалением
        labels_to_check = list(entity.labels)

        # Удаляем саму статью
        self.db.delete(entity)
        self.db.commit()  # Фиксируем удаление статьи и связей

        # Проверяем каждую метку: если у неё больше нет связанных статей — удаляем метку из БД
        for label in labels_to_check:
            # SQLAlchemy автоматически обновит список label.issues после коммита выше
            if len(label.issues) == 0:
                self.db.delete(label)

        self.db.commit()

    def _map_to_dto(self, entity: Issue) -> ArticleResponseTo:
        return ArticleResponseTo(
            id=entity.id,
            userId=entity.user_id,
            title=entity.title,
            content=entity.content,
            created=entity.created,
            modified=entity.modified
        )


class LabelService:
    def __init__(self, db: Session):
        self.repo = SQLAlchemyRepository(Label, db)

    def create(self, dto: LabelRequestTo) -> LabelResponseTo:
        entity = Label(name=dto.name)
        saved = self.repo.save(entity)
        return LabelResponseTo.model_validate(saved)

    def get_all(self, skip: int = 0, limit: int = 100, sort_by: str = 'id') -> list[LabelResponseTo]:
        labels = self.repo.find_all(skip=skip, limit=limit, sort_by=sort_by)
        return [LabelResponseTo.model_validate(l) for l in labels]

    def get_by_id(self, label_id: int) -> LabelResponseTo:
        entity = self.repo.find_by_id(label_id)
        if not entity:
            raise AppError(404, 40403, f"Label with id {label_id} not found")
        return LabelResponseTo.model_validate(entity)

    def update(self, label_id: int, dto: LabelRequestTo) -> LabelResponseTo:
        entity = self.repo.find_by_id(label_id)
        if not entity:
            raise AppError(404, 40403, f"Label with id {label_id} not found")
        entity.name = dto.name
        updated = self.repo.save(entity)
        return LabelResponseTo.model_validate(updated)

    def delete(self, label_id: int):
        if not self.repo.delete(label_id):
            raise AppError(404, 40403, f"Label with id {label_id} not found")


class CommentService:
    @staticmethod
    def create(dto: CommentRequestTo) -> CommentResponseTo:
        # Валидация Issue в Postgres всё еще здесь
        # (предположим, у тебя есть доступ к issue_repo или Session через Depends)

        with httpx.Client() as client:
            try:
                response = client.post(DISCUSSION_URL, json=dto.model_dump())
                if response.status_code == 201:
                    return CommentResponseTo(**response.json())
                raise AppError(response.status_code, 50001, "Discussion service error")
            except httpx.RequestError:
                raise AppError(503, 50301, "Discussion service unavailable")

    @staticmethod
    def get_by_id(comment_id: int) -> CommentResponseTo:
        with httpx.Client() as client:
            response = client.get(f"{DISCUSSION_URL}/{comment_id}")
            if response.status_code == 404:
                raise AppError(404, 40404, f"Comment {comment_id} not found")
            return CommentResponseTo(**response.json())

    @staticmethod
    def delete(comment_id: int):
        with httpx.Client() as client:
            response = client.delete(f"{DISCUSSION_URL}/{comment_id}")
            if response.status_code == 404:
                raise AppError(404, 40404, f"Comment {comment_id} not found")