from publisher.app.core.articles.repo import InMemoryArticleRepo
from publisher.app.core.writers.repo import InMemoryWriterRepo
from publisher.app.core.writers.dto import WriterRequestTo, WriterResponseTo
from publisher.app.core.writers.model import Writer
from publisher.app.core.exceptions import AppError


class WriterService:
    def __init__(self, repo: InMemoryWriterRepo, article_repo: InMemoryArticleRepo):
        self.repo = repo
        self.article_repo = article_repo

    def create_writer(self, dto: WriterRequestTo) -> WriterResponseTo:
        if self.repo.get_by_login(dto.login):
            raise AppError(status_code=400, message="Writer with this login already exists", suffix=2)
        model = Writer(id=0, login=dto.login, password=dto.password,
                       firstname=dto.firstname, lastname=dto.lastname)
        created = self.repo.create(model)
        return WriterResponseTo(id=created.id, login=created.login,
                                firstname=created.firstname, lastname=created.lastname)

    def get_by_id(self, id: int) -> WriterResponseTo:
        w = self.repo.get_by_id(id)
        if not w:
            raise AppError(status_code=404, message="Writer not found", suffix=1)
        return WriterResponseTo(id=w.id, login=w.login, firstname=w.firstname, lastname=w.lastname)

    def list_writers(self):
        return [WriterResponseTo(id=w.id, login=w.login, firstname=w.firstname, lastname=w.lastname)
                for w in self.repo.list_all()]

    def update_writer(self, id: int, dto: WriterRequestTo) -> WriterResponseTo:
        existing = self.repo.get_by_id(id)
        if not existing:
            raise AppError(status_code=404, message="Writer not found", suffix=3)

        by_login = self.repo.get_by_login(dto.login)
        if by_login and by_login.id != id:
            raise AppError(status_code=400, message="Another writer with this login already exists", suffix=4)

        existing.login = dto.login
        existing.password = dto.password
        existing.firstname = dto.firstname
        existing.lastname = dto.lastname

        try:
            updated = self.repo.update(id, existing)
        except KeyError:
            raise AppError(status_code=404, message="Writer not found", suffix=5)

        return WriterResponseTo(id=updated.id, login=updated.login,
                                firstname=updated.firstname, lastname=updated.lastname)

    def delete_writer(self, id: int) -> None:
        existing = self.repo.get_by_id(id)
        if not existing:
            raise AppError(status_code=404, message="Writer not found", suffix=6)
        try:
            self.repo.delete(id)
        except KeyError:
            raise AppError(status_code=404, message="Writer not found", suffix=7)
        return None

    def get_writer_by_article_id(self, article_id: int) -> WriterResponseTo:
        if not self.article_repo:
            raise AppError(status_code=500, message="Article repository is not configured", suffix=8)

        try:
            article = self.article_repo.get_by_id(article_id)
        except Exception:
            article = None

        if not article:
            raise AppError(status_code=404, message="Article not found", suffix=9)

        writer_id = getattr(article, "writer_id", None)
        if writer_id is None:
            raise AppError(status_code=500, message="Article has no writer_id", suffix=10)

        writer = self.repo.get_by_id(writer_id)
        if not writer:
            raise AppError(status_code=404, message="Writer not found for this article", suffix=11)

        return WriterResponseTo(id=writer.id, login=writer.login, firstname=writer.firstname, lastname=writer.lastname)