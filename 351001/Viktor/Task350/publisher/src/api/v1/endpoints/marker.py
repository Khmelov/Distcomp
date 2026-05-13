from fastapi import APIRouter, Depends, Query
from typing import List, Optional
from http import HTTPStatus

from Task350.publisher.src.api.v1.dep import get_marker_service
from Task350.publisher.src.schemas.marker import MarkerResponseTo, MarkerRequestTo
from Task350.publisher.src.services.marker import MarkerService

router = APIRouter(prefix="/markers")

@router.get("", response_model=List[MarkerResponseTo], status_code=HTTPStatus.OK)
async def get_all(
    page: int = Query(1, ge=1),
    size: int = Query(20, ge=1, le=100),
    sort: str = Query("id"),
    name: Optional[str] = Query(None, description="Filter by name (partial match)"),
    service: MarkerService = Depends(get_marker_service)
):
    return await service.get_all(page=page, size=size, sort=sort, name=name)

@router.get("/{marker_id}", response_model=MarkerResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(marker_id: str, service: MarkerService = Depends(get_marker_service)):
    return await service.get_one(marker_id)

@router.get("/by_tweet/{tweet_id}", response_model=List[MarkerResponseTo], status_code=HTTPStatus.OK)
async def get_by_tweet_id(tweet_id: int, service: MarkerService = Depends(get_marker_service)):
    return await service.get_markers_by_tweet_id(tweet_id)

@router.post("", response_model=MarkerResponseTo, status_code=HTTPStatus.CREATED)
async def create(dto: MarkerRequestTo, service: MarkerService = Depends(get_marker_service)):
    return await service.create(dto)

@router.put("/{marker_id}", response_model=MarkerResponseTo, status_code=HTTPStatus.OK)
async def update(marker_id: str, dto: MarkerRequestTo, service: MarkerService = Depends(get_marker_service)):
    dto_with_id = dto.model_copy(update={"id": int(marker_id)})
    return await service.update(dto_with_id)

@router.delete("/{marker_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(marker_id: str, service: MarkerService = Depends(get_marker_service)):
    await service.delete(marker_id)

    @router.put("", response_model=MarkerResponseTo, status_code=HTTPStatus.OK)
    async def update_with_id_in_body(dto: MarkerRequestTo, service: MarkerService = Depends(get_marker_service)):
        return await service.update(dto)