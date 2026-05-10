from fastapi import APIRouter, Depends, status
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.database import get_session
from publisher.exceptions.app_exceptions import MarkerNotFoundError
from publisher.models.marker import Marker
from publisher.repositories.sqlalchemy import SQLAlchemyRepository
from publisher.schemas.common import Page, PaginationParams
from publisher.schemas.marker import MarkerCreate, MarkerRead, MarkerUpdate
from publisher.services.marker_service import MarkerService


router = APIRouter(prefix="/api/v1.0/markers", tags=["markers"])


def get_marker_service(session: AsyncSession = Depends(get_session)) -> MarkerService:
    repo = SQLAlchemyRepository[Marker](Marker, session)
    return MarkerService(repo)


@router.post("", response_model=MarkerRead, status_code=status.HTTP_201_CREATED)
async def create_marker(
    dto: MarkerCreate,
    service: MarkerService = Depends(get_marker_service),
) -> MarkerRead:
    return await service.create(dto)


@router.get("", response_model=Page[MarkerRead])
async def list_markers(
    pagination: PaginationParams = Depends(),
    service: MarkerService = Depends(get_marker_service),
) -> Page[MarkerRead]:
    return await service.get_all(pagination)


@router.get("/{marker_id}", response_model=MarkerRead)
async def get_marker(
    marker_id: int,
    service: MarkerService = Depends(get_marker_service),
) -> MarkerRead:
    marker = await service.get(marker_id)
    if not marker:
        raise MarkerNotFoundError(marker_id)
    return marker


@router.put("/{marker_id}", response_model=MarkerRead)
async def update_marker(
    marker_id: int,
    dto: MarkerUpdate,
    service: MarkerService = Depends(get_marker_service),
) -> MarkerRead:
    marker = await service.update(marker_id, dto)
    if not marker:
        raise MarkerNotFoundError(marker_id)
    return marker


@router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_marker(
    marker_id: int,
    service: MarkerService = Depends(get_marker_service),
) -> None:
    deleted = await service.delete(marker_id)
    if not deleted:
        raise MarkerNotFoundError(marker_id)

