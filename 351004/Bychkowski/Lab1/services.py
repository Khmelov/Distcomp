from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from models import Writer, Article, Label, Post
from schemas import *
from repositories import PostgresRepository
from exceptions import AppError


class BaseService:
    def __init__(self, db: Session):
        self.db = db


class WriterService(BaseService):
    def __init__(self, db: Session):
        super().__init__(db)
        self.repo = PostgresRepository(Writer, db)

    def create(self, dto: WriterRequestTo) -> WriterResponseTo:
        try:
            entity = Writer(
                login=dto.login,
                password=dto.password,
                firstname=dto.firstname,
                lastname=dto.lastname
            )
            saved = self.repo.save(entity)
            return WriterResponseTo(id=saved.id, login=saved.login, firstname=saved.firstname, lastname=saved.lastname)
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40301, "Writer with this login already exists")

    def get_all(self) -> list[WriterResponseTo]:
        return [WriterResponseTo(id=w.id, login=w.login, firstname=w.firstname, lastname=w.lastname) for w in
                self.repo.find_all()]

    def get_by_id(self, id: int) -> WriterResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40401, f"Writer with id {id} not found")
        return WriterResponseTo(id=entity.id, login=entity.login, firstname=entity.firstname, lastname=entity.lastname)

    def update(self, id: int, dto: WriterRequestTo) -> WriterResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40401, f"Writer with id {id} not found")

        entity.login = dto.login
        entity.password = dto.password
        entity.firstname = dto.firstname
        entity.lastname = dto.lastname

        try:
            updated = self.repo.update(entity)
            return WriterResponseTo(id=updated.id, login=updated.login, firstname=updated.firstname,
                                    lastname=updated.lastname)
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40301, "Writer with this login already exists")

    def delete(self, id: int):
        if not self.repo.delete(id):
            raise AppError(404, 40401, f"Writer with id {id} not found")


class LabelService(BaseService):
    def __init__(self, db: Session):
        super().__init__(db)
        self.repo = PostgresRepository(Label, db)

    def create(self, dto: LabelRequestTo) -> LabelResponseTo:
        try:
            entity = Label(name=dto.name)
            saved = self.repo.save(entity)
            return LabelResponseTo(id=saved.id, name=saved.name)
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40302, "Label with this name already exists")

    def get_all(self) -> list[LabelResponseTo]:
        return [LabelResponseTo(id=l.id, name=l.name) for l in self.repo.find_all()]

    def get_by_id(self, id: int) -> LabelResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40403, f"Label with id {id} not found")
        return LabelResponseTo(id=entity.id, name=entity.name)

    def update(self, id: int, dto: LabelRequestTo) -> LabelResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40403, f"Label with id {id} not found")
        entity.name = dto.name
        try:
            updated = self.repo.update(entity)
            return LabelResponseTo(id=updated.id, name=updated.name)
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40302, "Label with this name already exists")

    def delete(self, id: int):
        if not self.repo.delete(id):
            raise AppError(404, 40403, f"Label with id {id} not found")


class ArticleService(BaseService):
    def __init__(self, db: Session):
        super().__init__(db)
        self.repo = PostgresRepository(Article, db)
        self.writer_repo = PostgresRepository(Writer, db)
        self.label_repo = PostgresRepository(Label, db)

    def create(self, dto: ArticleRequestTo) -> ArticleResponseTo:
        if not self.writer_repo.find_by_id(dto.writerId):
            raise AppError(400, 40003, f"Writer with id {dto.writerId} does not exist")

        labels = []
        if dto.labelIds:
            for label_id in dto.labelIds:
                label_entity = self.label_repo.find_by_id(label_id)
                if not label_entity:
                    raise AppError(400, 40005, f"Label with id {label_id} not found")
                labels.append(label_entity)

        try:
            entity = Article(
                writer_id=dto.writerId,
                title=dto.title,
                content=dto.content
            )
            entity.labels = labels

            saved = self.repo.save(entity)
            return self._map_to_dto(saved)
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40303, "Article with this title already exists")

    def get_all(self) -> list[ArticleResponseTo]:
        return [self._map_to_dto(a) for a in self.repo.find_all()]

    def get_by_id(self, id: int) -> ArticleResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40402, f"Article with id {id} not found")
        return self._map_to_dto(entity)

    def update(self, id: int, dto: ArticleRequestTo) -> ArticleResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40402, f"Article with id {id} not found")

        if not self.writer_repo.find_by_id(dto.writerId):
            raise AppError(400, 40003, f"Writer with id {dto.writerId} does not exist")

        entity.writer_id = dto.writerId
        entity.title = dto.title
        entity.content = dto.content

        if dto.labelIds is not None:
            new_labels = []
            for label_id in dto.labelIds:
                l = self.label_repo.find_by_id(label_id)
                if not l:
                    raise AppError(400, 40005, f"Label with id {label_id} not found")
                new_labels.append(l)
            entity.labels = new_labels

        try:
            updated = self.repo.update(entity)
            return self._map_to_dto(updated)
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40303, "Article with this title already exists")

    def delete(self, id: int):
        if not self.repo.delete(id):
            raise AppError(404, 40402, f"Article with id {id} not found")

    def _map_to_dto(self, entity: Article) -> ArticleResponseTo:
        current_labels = entity.labels if entity.labels else []
        label_dtos = [LabelResponseTo(id=l.id, name=l.name) for l in current_labels]

        return ArticleResponseTo(
            id=entity.id,
            writerId=entity.writer_id,
            title=entity.title,
            content=entity.content,
            created=entity.created,
            modified=entity.modified,
            labels=label_dtos
        )


class PostService(BaseService):
    def __init__(self, db: Session):
        super().__init__(db)
        self.repo = PostgresRepository(Post, db)
        self.article_repo = PostgresRepository(Article, db)

    def create(self, dto: PostRequestTo) -> PostResponseTo:
        if not self.article_repo.find_by_id(dto.articleId):
            raise AppError(400, 40004, f"Article with id {dto.articleId} does not exist")

        entity = Post(article_id=dto.articleId, content=dto.content)
        saved = self.repo.save(entity)
        return self._map_to_dto(saved)

    def get_all(self) -> list[PostResponseTo]:
        return [self._map_to_dto(p) for p in self.repo.find_all()]

    def get_by_id(self, id: int) -> PostResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40404, f"Post with id {id} not found")
        return self._map_to_dto(entity)

    def update(self, id: int, dto: PostRequestTo) -> PostResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40404, f"Post with id {id} not found")

        if not self.article_repo.find_by_id(dto.articleId):
            raise AppError(400, 40004, f"Article with id {dto.articleId} does not exist")

        entity.article_id = dto.articleId
        entity.content = dto.content
        updated = self.repo.update(entity)
        return self._map_to_dto(updated)

    def delete(self, id: int):
        if not self.repo.delete(id):
            raise AppError(404, 40404, f"Post with id {id} not found")

    def _map_to_dto(self, entity: Post) -> PostResponseTo:
        return PostResponseTo(
            id=entity.id,
            articleId=entity.article_id,
            content=entity.content
        )