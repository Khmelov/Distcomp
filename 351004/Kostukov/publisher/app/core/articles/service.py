# app/articles/service.py
from typing import List, Optional, Iterable, Set
from datetime import datetime
from publisher.app.core.articles.model import Article
from publisher.app.core.articles.dto import (
    ArticleRequestTo,
    ArticleResponseTo,
    MarkerShortTo,
)
from publisher.app.core.articles.repo import InMemoryArticleRepo
from publisher.app.core.exceptions import AppError

class ArticleService:
    def __init__(self, repo: InMemoryArticleRepo, writer_repo, marker_repo):
        self.repo = repo
        self.writer_repo = writer_repo
        self.marker_repo = marker_repo

    def _model_to_dto(self, model: Article) -> ArticleResponseTo:
        markers: List[MarkerShortTo] = []
        for mid in (model.marker_ids or []):
            marker = None
            if self.marker_repo:
                try:
                    marker = self.marker_repo.get_by_id(mid)
                except Exception:
                    marker = None
            if marker:
                markers.append(MarkerShortTo(id=marker.id, name=getattr(marker, "name", str(marker.id))))
            else:
                markers.append(MarkerShortTo(id=mid, name=""))
        dto = ArticleResponseTo(
            id=model.id,
            writerId=model.writer_id,
            title=model.title,
            content=model.content,
            markers=markers,
            created=model.created,
            modified=model.modified
        )
        return dto

    def _dto_to_model_for_create(self, dto: ArticleRequestTo) -> Article:
        now = datetime.now()
        marker_ids = list(dto.marker_ids) if dto.marker_ids else []
        return Article(
            id=0,
            writer_id=dto.writer_id,
            title=dto.title,
            content=dto.content,
            marker_ids=marker_ids,
            created=now,
            modified=now
        )

    def _validate_writer_exists(self, writer_id: int):
        if not self.writer_repo:
            raise AppError(status_code=500, message="Writer repository is not configured", suffix=10)
        w = self.writer_repo.get_by_id(writer_id)
        if w is None:
            raise AppError(status_code=400, message=f"Writer with id {writer_id} does not exist", suffix=11)

    def _validate_marker_ids_exist(self, marker_ids: Iterable[int]):
        if not marker_ids:
            return
        if not self.marker_repo:
            raise AppError(status_code=500, message="Marker repository is not configured", suffix=12)
        missing: List[int] = []
        for mid in marker_ids:
            if self.marker_repo.get_by_id(mid) is None:
                missing.append(mid)
        if missing:
            raise AppError(status_code=400, message=f"Markers not found: {missing}", suffix=13)

    def create_article(self, dto: ArticleRequestTo) -> ArticleResponseTo:
        "self._validate_writer_exists(dto.writer_id)"
        "if dto.marker_ids:"
        "self._validate_marker_ids_exist(dto.marker_ids)"

        model = self._dto_to_model_for_create(dto)
        created = self.repo.create(model)
        return self._model_to_dto(created)

    def get_article_by_id(self, id: int) -> ArticleResponseTo:
        a = self.repo.get_by_id(id)
        if not a:
            raise AppError(status_code=404, message="Article not found", suffix=14)
        return self._model_to_dto(a)

    def update_article(self, id: int, dto: ArticleRequestTo) -> ArticleResponseTo:
        existing = self.repo.get_by_id(id)
        if not existing:
            raise AppError(status_code=404, message="Article not found", suffix=15)
        "self._validate_writer_exists(dto.writer_id)"
        "if dto.marker_ids:"
        "self._validate_marker_ids_exist(dto.marker_ids)"
        existing.title = dto.title
        existing.content = dto.content
        existing.writer_id = dto.writer_id
        existing.marker_ids = list(dto.marker_ids) if dto.marker_ids else []
        existing.modified = datetime.utcnow()
        updated = self.repo.update(id, existing)
        return self._model_to_dto(updated)

    def delete_article(self, id: int) -> None:
        existing = self.repo.get_by_id(id)
        if not existing:
            raise AppError(status_code=404, message="Article not found", suffix=16)
        self.repo.delete(id)

    def get_markers_by_article_id(self, article_id: int) -> List[MarkerShortTo]:
        a = self.repo.get_by_id(article_id)
        if not a:
            raise AppError(status_code=404, message="Article not found", suffix=17)
        markers: List[MarkerShortTo] = []
        for mid in a.marker_ids:
            marker = None
            if self.marker_repo:
                marker = self.marker_repo.get_by_id(mid)
            if marker:
                markers.append(MarkerShortTo(id=marker.id, name=getattr(marker, "name", "")))
            else:
                markers.append(MarkerShortTo(id=mid, name=""))
        return markers

    def get_writer_by_article_id(self, article_id: int):
        a = self.repo.get_by_id(article_id)
        if not a:
            raise AppError(status_code=404, message="Article not found", suffix=18)
        if not self.writer_repo:
            raise AppError(status_code=500, message="Writer repository is not configured", suffix=19)
        writer = self.writer_repo.get_by_id(a.writer_id)
        if not writer:
            raise AppError(status_code=404, message="Writer not found for this article", suffix=20)
        return writer

    def search_articles(
        self,
        marker_names: Optional[List[str]] = None,
        marker_ids: Optional[List[int]] = None,
        writer_login: Optional[str] = None,
        title: Optional[str] = None,
        content: Optional[str] = None
    ) -> List[ArticleResponseTo]:
        resolved_marker_ids: Set[int] = set(marker_ids or [])
        if marker_names:
            if not self.marker_repo:
                raise AppError(status_code=500, message="Marker repository is not configured", suffix=21)
            ids_from_names = self.marker_repo.find_ids_by_names(marker_names)
            resolved_marker_ids.update(ids_from_names)

        writer_id = None
        if writer_login:
            if not self.writer_repo:
                raise AppError(status_code=500, message="Writer repository is not configured", suffix=22)
            writer = self.writer_repo.get_by_login(writer_login)
            if not writer:
                return []
            writer_id = writer.id

        def predicate(a: Article) -> bool:
            if writer_id is not None and a.writer_id != writer_id:
                return False
            if title and title.lower() not in a.title.lower():
                return False
            if content and content.lower() not in a.content.lower():
                return False
            if resolved_marker_ids:
                if not set(a.marker_ids).intersection(resolved_marker_ids):
                    return False
            return True

        matched = self.repo.list_filtered(predicate)
        return [self._model_to_dto(m) for m in matched]
