from typing import Any

from fastapi import APIRouter, Depends, status
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.database import get_session
from publisher.exceptions.app_exceptions import CreatorNotFoundError
from publisher.models.creator import Creator
from publisher.repositories.sqlalchemy import SQLAlchemyRepository
from publisher.schemas.common import Page, PaginationParams
from publisher.schemas.creator import CreatorCreate, CreatorRead, CreatorUpdate
from publisher.services.creator_service import CreatorService


router = APIRouter(prefix="/api/v1.0/creators", tags=["creators"])


def get_creator_service(session: AsyncSession = Depends(get_session)) -> CreatorService:
    repo = SQLAlchemyRepository[Creator](Creator, session)
    return CreatorService(repo)


@router.post("", response_model=CreatorRead, status_code=status.HTTP_201_CREATED)
async def create_creator(
    dto: CreatorCreate,
    service: CreatorService = Depends(get_creator_service),
) -> CreatorRead:
    return await service.create(dto)


@router.get("", response_model=Page[CreatorRead])
async def list_creators(
    pagination: PaginationParams = Depends(),
    service: CreatorService = Depends(get_creator_service),
) -> Page[CreatorRead]:
    return await service.get_all(pagination)


@router.get("/{creator_id}", response_model=CreatorRead)
async def get_creator(
    creator_id: int,
    service: CreatorService = Depends(get_creator_service),
) -> CreatorRead:
    creator = await service.get(creator_id)
    if not creator:
        raise CreatorNotFoundError(creator_id)
    return creator


@router.put("/{creator_id}", response_model=CreatorRead)
async def update_creator(
    creator_id: int,
    dto: CreatorUpdate,
    service: CreatorService = Depends(get_creator_service),
) -> CreatorRead:
    creator = await service.update(creator_id, dto)
    if not creator:
        raise CreatorNotFoundError(creator_id)
    return creator


@router.delete("/{creator_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_creator(
    creator_id: int,
    service: CreatorService = Depends(get_creator_service),
) -> None:
    deleted = await service.delete(creator_id)
    if not deleted:
        raise CreatorNotFoundError(creator_id)

