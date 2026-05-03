from fastapi import APIRouter, Depends, HTTPException, status, Query, Response
from typing import List, Optional
from dto.requests import UserRequestTo
from dto.responses import UserResponseTo
from services.user import UserService
from dependencies import get_user_service

router = APIRouter(prefix="/users", tags=["users"])


@router.get("", response_model=List[UserResponseTo])
def get_all_users(
    response: Response,
    page: int = Query(1, ge=1),
    size: int = Query(10, ge=1, le=100),
    sortBy: str = Query("id", pattern="^(id|login|firstname|lastname)$"),
    order: str = Query("asc", pattern="^(asc|desc)$"),
    login: Optional[str] = Query(None),
    firstname: Optional[str] = Query(None),
    lastname: Optional[str] = Query(None),
    service: UserService = Depends(get_user_service),
):
    filters = {}
    if login:
        filters["login"] = login
    if firstname:
        filters["firstname"] = firstname
    if lastname:
        filters["lastname"] = lastname
    users = service.get_list(
        filters=filters, page=page, size=size, sort_by=sortBy, order=order
    )
    total = service.count(filters)
    response.headers["X-Total-Count"] = str(total)
    return users


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
