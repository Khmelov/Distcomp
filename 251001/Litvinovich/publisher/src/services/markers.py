from fastapi import HTTPException

from src.models.models import Marker
from src.schemas.markers import MarkerRequestToAdd, MarkerResponseTo, MarkerRequestToUpdate
from src.utils.repository import AbstractRepository, NotFoundError


class MarkersService:
    def __init__(self, markers_repo: AbstractRepository):
        self.markers_repo: AbstractRepository = markers_repo

    async def create_marker(self, marker: MarkerRequestToAdd) -> MarkerResponseTo:
        marker_model = Marker(
            name=marker.name
        )

        try:
            created_model = await self.markers_repo.create(marker_model)
            return MarkerResponseTo.model_validate(created_model)
        except ValueError as e:
            raise HTTPException(status_code=403, detail=str(e))

    async def get_markers(self) -> list[MarkerResponseTo]:
        markers = await self.markers_repo.get_all()
        return [MarkerResponseTo.model_validate(marker) for marker in markers]

    async def get_marker_by_id(self, marker_id: int) -> MarkerResponseTo:
        try:
            marker = await self.markers_repo.get_by_id(marker_id)
            return MarkerResponseTo.model_validate(marker)
        except NotFoundError:
            raise HTTPException(status_code=404, detail="Marker not found")

    async def delete_marker(self, marker_id: int):
        try:
            await self.markers_repo.delete(marker_id)
        except NotFoundError:
            raise HTTPException(status_code=404, detail="Marker not found")

    async def update_marker(self, marker: MarkerRequestToUpdate) -> MarkerResponseTo:
        try:
            existing_marker = await self.markers_repo.get_by_id(marker.id)

            existing_marker.name = marker.name

            updated_marker = await self.markers_repo.update(existing_marker)
            return MarkerResponseTo.model_validate(updated_marker)
        except NotFoundError:
            raise HTTPException(status_code=404, detail="Marker not found")