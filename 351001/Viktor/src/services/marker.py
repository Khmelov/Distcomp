from typing import List

from src.core.constants import ErrorStatus
from src.core.errors import HttpNotFoundError
from src.core.errors.messages import MarkerErrorMessage
from src.domain.models import Marker
from src.domain.repositories.interfaces import Repository
from src.schemas.marker import MarkerResponseTo, MarkerRequestTo

class MarkerService:
    def __init__(self, repo: Repository[Marker]) -> None:
        self._repo = repo

    def get_one(self, marker_id_str: str) -> MarkerResponseTo:
        try:
            marker_id = int(marker_id_str)
        except ValueError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        try:
            marker = self._repo.get_one(marker_id)
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return MarkerResponseTo.model_validate(marker)

    def get_all(self) -> List[MarkerResponseTo]:
        markers = self._repo.get_all()
        return [MarkerResponseTo.model_validate(m) for m in markers]

    def create(self, dto: MarkerRequestTo) -> MarkerResponseTo:
        marker = Marker(
            id=0,
            name=dto.name
        )
        created = self._repo.create(marker)
        return MarkerResponseTo.model_validate(created)

    def update(self, marker_id_str: str, dto: MarkerRequestTo) -> MarkerResponseTo:
        try:
            marker_id = int(marker_id_str)
        except ValueError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        marker = Marker(
            id=marker_id,
            name=dto.name
        )
        try:
            updated = self._repo.update(marker)
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return MarkerResponseTo.model_validate(updated)

    def delete(self, marker_id_str: str) -> None:
        try:
            marker_id = int(marker_id_str)
        except ValueError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        try:
            self._repo.delete(marker_id)
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)