from typing import List
from app.core.markers.repo import InMemoryMarkerRepo
from app.core.markers.dto import MarkerRequestTo, MarkerResponseTo
from app.core.markers.model import Marker
from app.core.exceptions import AppError
from datetime import datetime

class MarkerService:

    def __init__(self, repo: InMemoryMarkerRepo, article_repo=None):
        self.repo = repo
        self.article_repo = article_repo

    def create_marker(self, dto: MarkerRequestTo) -> MarkerResponseTo:
        if self.repo.get_by_name(dto.name):
            raise AppError(status_code=400, message=f"Marker with name '{dto.name}' already exists", suffix=2)

        model = Marker(id=0, name=dto.name, created_at=datetime.utcnow())
        created = self.repo.create(model)
        return MarkerResponseTo(id=created.id, name=created.name)

    def get_marker_by_id(self, id: int) -> MarkerResponseTo:
        m = self.repo.get_by_id(id)
        if not m:
            raise AppError(status_code=404, message="Marker not found", suffix=1)
        return MarkerResponseTo(id=m.id, name=m.name)

    def list_markers(self) -> List[MarkerResponseTo]:
        return [MarkerResponseTo(id=m.id, name=m.name) for m in self.repo.list_all()]

    def update_marker(self, id: int, dto: MarkerRequestTo) -> MarkerResponseTo:
        existing = self.repo.get_by_id(id)
        if not existing:
            raise AppError(status_code=404, message="Marker not found", suffix=3)
        other = self.repo.get_by_name(dto.name)
        if other and other.id != id:
            raise AppError(status_code=400, message=f"Another marker with name '{dto.name}' exists", suffix=4)
        existing.name = dto.name
        updated = self.repo.update(id, existing)
        return MarkerResponseTo(id=updated.id, name=updated.name)

    def delete_marker(self, id: int) -> None:
        existing = self.repo.get_by_id(id)
        if not existing:
            raise AppError(status_code=404, message="Marker not found", suffix=5)

        if self.article_repo:
            for article in self.article_repo.list_all():
                if id in getattr(article, "marker_ids", []):
                    article.marker_ids = [mid for mid in article.marker_ids if mid != id]
                    try:
                        self.article_repo.update(article.id, article)
                    except KeyError:
                        continue

        self.repo.delete(id)

    def find_ids_by_names(self, names: List[str]) -> List[int]:
        return self.repo.find_ids_by_names(names)
