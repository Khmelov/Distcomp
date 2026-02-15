from datetime import datetime, timezone
from models import Writer, Article, Label, Post
from schemas import (
    WriterRequestTo, WriterResponseTo,
    ArticleRequestTo, ArticleResponseTo,
    LabelRequestTo, LabelResponseTo,
    PostRequestTo, PostResponseTo
)
from repositories import writer_repo, article_repo, label_repo, post_repo
from exceptions import AppError


# --- Writer Service ---
class WriterService:
    @staticmethod
    def create(dto: WriterRequestTo) -> WriterResponseTo:
        for w in writer_repo.find_all():
            if w.login == dto.login:
                raise AppError(400, 40002, "Writer with this login already exists")

        entity = Writer(id=None, **dto.model_dump())
        saved = writer_repo.save(entity)
        return WriterResponseTo(**saved.__dict__)

    @staticmethod
    def get_all() -> list[WriterResponseTo]:
        return [WriterResponseTo(**w.__dict__) for w in writer_repo.find_all()]

    @staticmethod
    def get_by_id(id: int) -> WriterResponseTo:
        entity = writer_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40401, f"Writer with id {id} not found")
        return WriterResponseTo(**entity.__dict__)

    @staticmethod
    def update(id: int, dto: WriterRequestTo) -> WriterResponseTo:
        existing = writer_repo.find_by_id(id)
        if not existing:
            raise AppError(404, 40401, f"Writer with id {id} not found")

        # Обновляем поля
        existing.login = dto.login
        existing.password = dto.password
        existing.firstname = dto.firstname
        existing.lastname = dto.lastname

        updated = writer_repo.update(id, existing)
        return WriterResponseTo(**updated.__dict__)

    @staticmethod
    def delete(id: int):
        if not writer_repo.delete(id):
            raise AppError(404, 40401, f"Writer with id {id} not found")


# --- Article Service ---
class ArticleService:
    @staticmethod
    def create(dto: ArticleRequestTo) -> ArticleResponseTo:
        # Проверка существования Writer
        if not writer_repo.find_by_id(dto.writerId):
            raise AppError(400, 40003, f"Writer with id {dto.writerId} does not exist")

        now = datetime.now(timezone.utc)
        entity = Article(
            id=None,
            writer_id=dto.writerId,
            title=dto.title,
            content=dto.content,
            created=now,
            modified=now
        )
        saved = article_repo.save(entity)
        return ArticleService._map_to_dto(saved)

    @staticmethod
    def get_all() -> list[ArticleResponseTo]:
        return [ArticleService._map_to_dto(a) for a in article_repo.find_all()]

    @staticmethod
    def get_by_id(id: int) -> ArticleResponseTo:
        entity = article_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40402, f"Article with id {id} not found")
        return ArticleService._map_to_dto(entity)

    @staticmethod
    def update(id: int, dto: ArticleRequestTo) -> ArticleResponseTo:
        existing = article_repo.find_by_id(id)
        if not existing:
            raise AppError(404, 40402, f"Article with id {id} not found")

        if not writer_repo.find_by_id(dto.writerId):
            raise AppError(400, 40003, f"Writer with id {dto.writerId} does not exist")

        existing.writer_id = dto.writerId
        existing.title = dto.title
        existing.content = dto.content
        existing.modified = datetime.now(timezone.utc)

        updated = article_repo.update(id, existing)
        return ArticleService._map_to_dto(updated)

    @staticmethod
    def delete(id: int):
        if not article_repo.delete(id):
            raise AppError(404, 40402, f"Article with id {id} not found")

    @staticmethod
    def _map_to_dto(entity: Article) -> ArticleResponseTo:
        return ArticleResponseTo(
            id=entity.id,
            writerId=entity.writer_id,
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


# --- Post Service ---
class PostService:
    @staticmethod
    def create(dto: PostRequestTo) -> PostResponseTo:
        if not article_repo.find_by_id(dto.articleId):
            raise AppError(400, 40004, f"Article with id {dto.articleId} does not exist")

        entity = Post(id=None, article_id=dto.articleId, content=dto.content)
        saved = post_repo.save(entity)
        return PostService._map_to_dto(saved)

    @staticmethod
    def get_all() -> list[PostResponseTo]:
        return [PostService._map_to_dto(p) for p in post_repo.find_all()]

    @staticmethod
    def get_by_id(id: int) -> PostResponseTo:
        entity = post_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40404, f"Post with id {id} not found")
        return PostService._map_to_dto(entity)

    @staticmethod
    def update(id: int, dto: PostRequestTo) -> PostResponseTo:
        existing = post_repo.find_by_id(id)
        if not existing:
            raise AppError(404, 40404, f"Post with id {id} not found")

        if not article_repo.find_by_id(dto.articleId):
            raise AppError(400, 40004, f"Article with id {dto.articleId} does not exist")

        existing.article_id = dto.articleId
        existing.content = dto.content
        updated = post_repo.update(id, existing)
        return PostService._map_to_dto(updated)

    @staticmethod
    def delete(id: int):
        if not post_repo.delete(id):
            raise AppError(404, 40404, f"Post with id {id} not found")

    @staticmethod
    def _map_to_dto(entity: Post) -> PostResponseTo:
        return PostResponseTo(
            id=entity.id,
            articleId=entity.article_id,
            content=entity.content
        )