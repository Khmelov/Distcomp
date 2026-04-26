from typing import List, Optional

from Task320.src.core.constants import ErrorStatus
from Task320.src.core.errors import HttpNotFoundError, HttpBadRequestError
from Task320.src.core.errors.messages import MarkerErrorMessage
from Task320.src.domain.models import Marker
from Task320.src.domain.repositories.interfaces import Repository
from Task320.src.schemas.marker import MarkerResponseTo, MarkerRequestTo

class MarkerService:
    def __init__(self, repo: Repository[Marker]) -> None:
        self._repo = repo

    async def get_one(self, marker_id_str: str) -> MarkerResponseTo:
        try:
            marker_id = int(marker_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid marker ID format", ErrorStatus.BAD_REQUEST)
        try:
            marker = await self._repo.get_one(marker_id)
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return MarkerResponseTo.model_validate(marker)

    async def get_all(
        self,
        page: int = 1,
        size: int = 20,
        sort: str = "id",
        name: Optional[str] = None
    ) -> List[MarkerResponseTo]:
        markers = await self._repo.get_all(page=page, size=size, sort=sort, name=name)
        return [MarkerResponseTo.model_validate(m) for m in markers]

    async def create(self, dto: MarkerRequestTo) -> MarkerResponseTo:
        marker = Marker(id=0, name=dto.name)
        created = await self._repo.create(marker)
        return MarkerResponseTo.model_validate(created)

    async def update(self, marker_id_str: str, dto: MarkerRequestTo) -> MarkerResponseTo:
        try:
            marker_id = int(marker_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid marker ID format", ErrorStatus.BAD_REQUEST)
        marker = Marker(id=marker_id, name=dto.name)
        try:
            updated = await self._repo.update(marker)
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return MarkerResponseTo.model_validate(updated)

    async def delete(self, marker_id_str: str) -> None:
        try:
            marker_id = int(marker_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid marker ID format", ErrorStatus.BAD_REQUEST)
        try:
            await self._repo.delete(marker_id)
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)

    async def get_markers_by_tweet_id(self, tweet_id: int) -> List[MarkerResponseTo]:
        markers = await self._repo.get_by_tweet_id(tweet_id)
        return [MarkerResponseTo.model_validate(m) for m in markers]

    async def update(self, dto: MarkerRequestTo) -> MarkerResponseTo:
        if dto.id is None:
            raise HttpBadRequestError("Missing marker ID", ErrorStatus.BAD_REQUEST)
        try:
            await self._repo.get_one(dto.id)  # проверка существования
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        marker = Marker(id=dto.id, name=dto.name)
        updated = await self._repo.update(marker)
        return MarkerResponseTo.model_validate(updated)