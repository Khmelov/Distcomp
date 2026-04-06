from fastapi import APIRouter, Response, status

from app.dto.user import UserRequestTo, UserResponseTo
from app.services import user_service

router = APIRouter(prefix="/api/v1.0/users", tags=["users"])


@router.get("", response_model=list[UserResponseTo])
def get_users() -> list[UserResponseTo]:
    return user_service.get_all()


@router.get("/{user_id}", response_model=UserResponseTo)
def get_user(user_id: int) -> UserResponseTo:
    return user_service.get_by_id(user_id)


@router.post("", response_model=UserResponseTo, status_code=status.HTTP_201_CREATED)
def create_user(request: UserRequestTo) -> UserResponseTo:
    return user_service.create(request)


@router.put("", response_model=UserResponseTo)
def update_user(request: UserRequestTo) -> UserResponseTo:
    return user_service.update(request)


@router.delete("/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(user_id: int) -> Response:
    user_service.delete(user_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
