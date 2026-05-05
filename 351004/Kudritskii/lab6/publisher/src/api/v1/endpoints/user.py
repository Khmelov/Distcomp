from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.deps import get_user_service
from src.schemas.user import UserResponseTo, UserRequestTo
from src.services.user import UserService

router = APIRouter(prefix="/users")

@router.get("", response_model=List[UserResponseTo], status_code=HTTPStatus.OK)
async def get_all(service: UserService = Depends(get_user_service)):
    return await service.get_all()

@router.get("/{user_id}", response_model=UserResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(user_id: int, service: UserService = Depends(get_user_service)):
    return await service.get_one(user_id)

@router.post("", response_model=UserResponseTo, status_code=HTTPStatus.CREATED)
async def create(request: UserRequestTo, service: UserService = Depends(get_user_service)):
    return await service.create(request)


@router.put("/{user_id}", response_model=UserResponseTo, status_code=HTTPStatus.OK)
async def update(user_id: int, dto: UserRequestTo, service: UserService = Depends(get_user_service)):
    return await service.update(user_id, dto)

@router.delete("/{user_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(user_id: int, service: UserService = Depends(get_user_service)):
    await service.delete(user_id)