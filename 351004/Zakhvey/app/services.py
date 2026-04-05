from datetime import datetime, timezone
from models import User, Issue, Label, Comment
from schemas import (
    UserRequestTo, UserResponseTo,
    IssueRequestTo, ArticleResponseTo,
    LabelRequestTo, LabelResponseTo,
    CommentRequestTo, CommentResponseTo
)
from repositories import user_repo, issue_repo, label_repo, comment_repo
from exceptions import AppError
from repositories import issue_label_repo # Добавьте в импорты
from models import IssueLabel


# --- User Service ---
class UserService:
    @staticmethod
    def create(dto: UserRequestTo) -> UserResponseTo:
        for w in user_repo.find_all():
            if w.login == dto.login:
                raise AppError(400, 40002, "User with this login already exists")

        entity = User(id=None, **dto.model_dump())
        saved = user_repo.save(entity)
        return UserResponseTo(**saved.__dict__)

    @staticmethod
    def get_all() -> list[UserResponseTo]:
        return [UserResponseTo(**w.__dict__) for w in user_repo.find_all()]

    @staticmethod
    def get_by_id(id: int) -> UserResponseTo:
        entity = user_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40401, f"User with id {id} not found")
        return UserResponseTo(**entity.__dict__)

    @staticmethod
    def update(id: int, dto: UserRequestTo) -> UserResponseTo:
        existing = user_repo.find_by_id(id)
        if not existing:
            raise AppError(404, 40401, f"User with id {id} not found")

        # Обновляем поля
        existing.login = dto.login
        existing.password = dto.password
        existing.firstname = dto.firstname
        existing.lastname = dto.lastname

        updated = user_repo.update(id, existing)
        return UserResponseTo(**updated.__dict__)

    @staticmethod
    def delete(id: int):
        if not user_repo.delete(id):
            raise AppError(404, 40401, f"User with id {id} not found")


# --- Issue Service ---
class IssueService:
    @staticmethod
    def create(dto: IssueRequestTo) -> ArticleResponseTo:
        # Проверка существования Writer
        if not user_repo.find_by_id(dto.userId):
            raise AppError(400, 40003, f"User with id {dto.userId} does not exist")

        now = datetime.now(timezone.utc)
        entity = Issue(
            id=None,
            userId=dto.userId,
            title=dto.title,
            content=dto.content,
            created=now,
            modified=now
        )
        saved = issue_repo.save(entity)
        for label_id in dto.labelIds:
            if label_repo.find_by_id(label_id):
                link = IssueLabel(id=None, issueId=saved.id, labelId=label_id)
                issue_label_repo.save(link)
        return IssueService._map_to_dto(saved)

    @staticmethod
    def get_labels_for_issue(issue_id: int) -> list[LabelResponseTo]:
        if not issue_repo.find_by_id(issue_id):
            raise AppError(404, 40402, f"Issue with id {issue_id} not found")

        # Ищем все связи для данного issueId
        links = [link for link in issue_label_repo.find_all() if link.issueId == issue_id]
        labels = []
        for link in links:
            l_entity = label_repo.find_by_id(link.labelId)
            if l_entity:
                labels.append(LabelResponseTo(**l_entity.__dict__))
        return labels

    @staticmethod
    def get_all() -> list[ArticleResponseTo]:
        return [IssueService._map_to_dto(a) for a in issue_repo.find_all()]

    @staticmethod
    def get_by_id(id: int) -> ArticleResponseTo:
        entity = issue_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40402, f"Issue with id {id} not found")
        return IssueService._map_to_dto(entity)

    @staticmethod
    def update(id: int, dto: IssueRequestTo) -> ArticleResponseTo:
        existing = issue_repo.find_by_id(id)
        if not existing:
            raise AppError(404, 40402, f"Issue with id {id} not found")

        if not user_repo.find_by_id(dto.userId):
            raise AppError(400, 40003, f"User with id {dto.userId} does not exist")

        existing.userId = dto.userId
        existing.title = dto.title
        existing.content = dto.content
        existing.modified = datetime.now(timezone.utc)

        updated = issue_repo.update(id, existing)
        return IssueService._map_to_dto(updated)

    @staticmethod
    def delete(id: int):
        if not issue_repo.delete(id):
            raise AppError(404, 40402, f"Issue with id {id} not found")
        all_links = issue_label_repo.find_all()
        for link in all_links:
            if link.issueId == id:
                issue_label_repo.delete(link.id)

    @staticmethod
    def _map_to_dto(entity: Issue) -> ArticleResponseTo:
        return ArticleResponseTo(
            id=entity.id,
            userId=entity.userId,
            title=entity.title,
            content=entity.content,
            created=entity.created,
            modified=entity.modified
        )


# --- Label Service ---
class LabelService:
    @staticmethod
    def create(dto: LabelRequestTo) -> LabelResponseTo:
        entity = Label(id=None, name=dto.name)
        saved = label_repo.save(entity)
        return LabelResponseTo(**saved.__dict__)

    @staticmethod
    def get_all() -> list[LabelResponseTo]:
        return [LabelResponseTo(**l.__dict__) for l in label_repo.find_all()]

    @staticmethod
    def get_by_id(id: int) -> LabelResponseTo:
        entity = label_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40403, f"Label with id {id} not found")
        return LabelResponseTo(**entity.__dict__)

    @staticmethod
    def update(id: int, dto: LabelRequestTo) -> LabelResponseTo:
        existing = label_repo.find_by_id(id)
        if not existing:
            raise AppError(404, 40403, f"Label with id {id} not found")
        existing.name = dto.name
        updated = label_repo.update(id, existing)
        return LabelResponseTo(**updated.__dict__)

    @staticmethod
    def delete(id: int):
        if not label_repo.delete(id):
            raise AppError(404, 40403, f"Label with id {id} not found")


# --- Comment Service ---
class CommentService:
    @staticmethod
    def create(dto: CommentRequestTo) -> CommentResponseTo:
        if not issue_repo.find_by_id(dto.issueId):
            raise AppError(400, 40004, f"Issue with id {dto.issueId} does not exist")

        entity = Comment(id=None, issueId=dto.issueId, content=dto.content)
        saved = comment_repo.save(entity)
        return CommentService._map_to_dto(saved)

    @staticmethod
    def get_all() -> list[CommentResponseTo]:
        return [CommentService._map_to_dto(p) for p in comment_repo.find_all()]

    @staticmethod
    def get_by_id(id: int) -> CommentResponseTo:
        entity = comment_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40404, f"Comment with id {id} not found")
        return CommentService._map_to_dto(entity)

    @staticmethod
    def update(id: int, dto: CommentRequestTo) -> CommentResponseTo:
        existing = comment_repo.find_by_id(id)
        if not existing:
            raise AppError(404, 40404, f"Comment with id {id} not found")

        if not issue_repo.find_by_id(dto.issueId):
            raise AppError(400, 40004, f"Issue with id {dto.issueId} does not exist")

        existing.issueId = dto.issueId
        existing.content = dto.content
        updated = comment_repo.update(id, existing)
        return CommentService._map_to_dto(updated)

    @staticmethod
    def delete(id: int):
        if not comment_repo.delete(id):
            raise AppError(404, 40404, f"Comment with id {id} not found")

    @staticmethod
    def _map_to_dto(entity: Comment) -> CommentResponseTo:
        return CommentResponseTo(
            id=entity.id,
            issueId=entity.issueId,
            content=entity.content
        )