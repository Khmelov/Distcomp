from fastapi import APIRouter, status
from app.dto.user import UserRequestTo, UserResponseTo
from app.services.user_service import UserService

router = APIRouter()
service = UserService()


@router.post("/users",
             response_model=UserResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_user(dto: UserRequestTo):
    return service.create(dto)


@router.get("/users", response_model=list[UserResponseTo])
def get_users():
    return service.find_all()


@router.get("/users/{id}", response_model=UserResponseTo)
def get_user(id: int):
    return service.find_by_id(id)


@router.put("/users/{id}", response_model=UserResponseTo)
def update_user(id: int, dto: UserRequestTo):
    return service.update(id, dto)


@router.delete("/users/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(id: int):
    service.delete(id)
