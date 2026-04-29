from fastapi import APIRouter, Depends, HTTPException, status
from typing import List
from dto.requests import UserRequestTo
from dto.responses import UserResponseTo
from services.user import UserService
from dependencies import get_user_service
from fastapi import Body

router = APIRouter(prefix="/users", tags=["users"])


@router.get("", response_model=List[UserResponseTo])
def get_all_users(service: UserService = Depends(get_user_service)):
    return service.get_all()


@router.get("/{id}", response_model=UserResponseTo)
def get_user(id: int, service: UserService = Depends(get_user_service)):
    user = service.get(id)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="User not found"
        )
    return user


@router.post("", response_model=UserResponseTo, status_code=status.HTTP_201_CREATED)
def create_user(
    request: UserRequestTo, service: UserService = Depends(get_user_service)
):
    return service.create(request)


@router.put("/{id}", response_model=UserResponseTo)
def update_user(
    id: int, request: UserRequestTo, service: UserService = Depends(get_user_service)
):
    try:
        return service.update(id, request)
    except ValueError as e:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(e))


@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(id: int, service: UserService = Depends(get_user_service)):
    if not service.delete(id):
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="User not found"
        )
