from src.database.uow import UnitOfWork
from src.dto.marker import MarkerRequestTo, MarkerResponseTo
from src.exceptions import EntityAlreadyExistsException, EntityNotFoundException
from src.models.marker import Marker
from src.repositories.marker import AbstractMarkerRepository


class MarkerService:

    def __init__(self, repository: AbstractMarkerRepository, uow: UnitOfWork) -> None:
        self._repo = repository
        self._uow = uow

    async def get_by_id(self, marker_id: int) -> MarkerResponseTo:
        marker = await self._repo.get_by_id(marker_id)
        if marker is None:
            raise EntityNotFoundException("Marker", marker_id)
        return MarkerResponseTo.model_validate(marker)

    async def get_all(self) -> list[MarkerResponseTo]:
        markers = await self._repo.get_all()
        return [MarkerResponseTo.model_validate(m) for m in markers]

    async def create(self, data: MarkerRequestTo) -> MarkerResponseTo:
        existing = await self._repo.get_by_name(data.name)
        if existing is not None:
            raise EntityAlreadyExistsException("Marker", "name", data.name)
        marker = Marker(name=data.name)
        created = await self._repo.create(marker)
        await self._uow.commit()
        return MarkerResponseTo.model_validate(created)

    async def update(self, marker_id: int, data: MarkerRequestTo) -> MarkerResponseTo:
        existing = await self._repo.get_by_name(data.name)
        if existing is not None and existing.id != marker_id:
            raise EntityAlreadyExistsException("Marker", "name", data.name)
        marker = Marker(name=data.name)
        marker.id = marker_id
        updated = await self._repo.update(marker)
        if updated is None:
            raise EntityNotFoundException("Marker", marker_id)
        await self._uow.commit()
        return MarkerResponseTo.model_validate(updated)

    async def delete(self, marker_id: int) -> None:
        deleted = await self._repo.delete(marker_id)
        if not deleted:
            raise EntityNotFoundException("Marker", marker_id)
        await self._uow.commit()
