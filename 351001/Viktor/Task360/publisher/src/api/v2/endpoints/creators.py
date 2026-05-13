from fastapi import APIRouter, Depends, HTTPException, Query
from typing import List, Optional
from Task360.publisher.src.api.v2.dep import get_current_user, require_role
from Task360.publisher.src.services.auth import hash_password
from Task360.publisher.src.api.v1.dep import get_creator_service
from Task360.publisher.src.schemas.creator import CreatorRequestTo, CreatorResponseTo
from Task360.publisher.src.services.creator import CreatorService

router = APIRouter(prefix="/creators")

@router.post("", response_model=CreatorResponseTo, status_code=201)
async def register_creator(dto: CreatorRequestTo, service: CreatorService = Depends(get_creator_service)):
    # хешируем пароль
    dto.password = hash_password(dto.password)
    return await service.create(dto)

@router.get("", response_model=List[CreatorResponseTo])
async def get_all_creators(
    page: int = Query(1, ge=1),
    size: int = Query(20, ge=1, le=100),
    sort: str = Query("id"),
    login: Optional[str] = None,
    firstname: Optional[str] = None,
    lastname: Optional[str] = None,
    current_user = Depends(get_current_user),
    service: CreatorService = Depends(get_creator_service)
):
    return await service.get_all(page, size, sort, login, firstname, lastname)

@router.get("/{id}", response_model=CreatorResponseTo)
async def get_creator(id: str, current_user = Depends(get_current_user), service: CreatorService = Depends(get_creator_service)):
    return await service.get_one(id)

@router.put("/{id}", response_model=CreatorResponseTo)
async def update_creator(
    id: str,
    dto: CreatorRequestTo,
    current_user = Depends(get_current_user),
    service: CreatorService = Depends(get_creator_service)
):
    # Проверка прав: ADMIN или владелец
    if current_user.role != 'ADMIN' and str(current_user.id) != id:
        raise HTTPException(status_code=403, detail="Not allowed")
    if dto.password:
        dto.password = hash_password(dto.password)
    return await service.update(id, dto)

@router.delete("/{id}", status_code=204)
async def delete_creator(
    id: str,
    current_user = Depends(get_current_user),
    service: CreatorService = Depends(get_creator_service)
):
    if current_user.role != 'ADMIN' and str(current_user.id) != id:
        raise HTTPException(status_code=403, detail="Not allowed")
    await service.delete(id)