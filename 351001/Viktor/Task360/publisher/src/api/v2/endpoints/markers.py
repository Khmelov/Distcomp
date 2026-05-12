from fastapi import APIRouter, Depends, HTTPException, Query
from typing import List, Optional
from Task360.publisher.src.api.v2.dep import get_current_user, require_role
from Task360.publisher.src.api.v1.dep import get_marker_service
from Task360.publisher.src.schemas.marker import MarkerRequestTo, MarkerResponseTo
from Task360.publisher.src.services.marker import MarkerService

router = APIRouter(prefix="/markers")

# Все GET-запросы доступны аутентифицированным пользователям (и ADMIN, и CUSTOMER)
@router.get("", response_model=List[MarkerResponseTo])
async def get_all_markers(
    page: int = Query(1, ge=1),
    size: int = Query(20, ge=1, le=100),
    sort: str = Query("id"),
    name: Optional[str] = Query(None),
    current_user = Depends(get_current_user),
    service: MarkerService = Depends(get_marker_service)
):
    return await service.get_all(page, size, sort, name)

@router.get("/{id}", response_model=MarkerResponseTo)
async def get_marker(
    id: str,
    current_user = Depends(get_current_user),
    service: MarkerService = Depends(get_marker_service)
):
    return await service.get_one(id)

# Создание, обновление, удаление – только для ADMIN
@router.post("", response_model=MarkerResponseTo, status_code=201)
async def create_marker(
    dto: MarkerRequestTo,
    current_user = Depends(require_role("ADMIN")),
    service: MarkerService = Depends(get_marker_service)
):
    return await service.create(dto)

@router.put("/{id}", response_model=MarkerResponseTo)
async def update_marker(
    id: str,
    dto: MarkerRequestTo,
    current_user = Depends(require_role("ADMIN")),
    service: MarkerService = Depends(get_marker_service)
):
    # Обновляем маркер по id из пути
    return await service.update(id, dto)

@router.put("", response_model=MarkerResponseTo)
async def update_marker_with_body(
    dto: MarkerRequestTo,
    current_user = Depends(require_role("ADMIN")),
    service: MarkerService = Depends(get_marker_service)
):
    # Альтернативный PUT с id в теле (если требуется)
    return await service.update_from_body(dto)

@router.delete("/{id}", status_code=204)
async def delete_marker(
    id: str,
    current_user = Depends(require_role("ADMIN")),
    service: MarkerService = Depends(get_marker_service)
):
    await service.delete(id)

# Дополнительный эндпоинт – получение маркеров по твиту (доступен всем аутентифицированным)
@router.get("/by_tweet/{tweet_id}", response_model=List[MarkerResponseTo])
async def get_markers_by_tweet(
    tweet_id: int,
    current_user = Depends(get_current_user),
    service: MarkerService = Depends(get_marker_service)
):
    return await service.get_markers_by_tweet_id(tweet_id)