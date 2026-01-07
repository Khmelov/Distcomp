from fastapi import APIRouter, HTTPException
from app.schemas.user import UserCreate, UserRead, UserUpdate
from app.services.user_service import UserService
from app.repositories.user_repository import UserRepository

router = APIRouter()

user_repo = UserRepository()
user_service = UserService(user_repo)

@router.post("", response_model=UserRead, status_code=201)
def create_user(user_create: UserCreate):
    return user_service.create_user(user_create)

@router.get("/{user_id}", response_model=UserRead)
def get_user(user_id: int):
    user = user_service.get_user(user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user

@router.get("", response_model=list[UserRead])
def list_users():
    return user_service.list_users()

@router.delete("/{user_id}", status_code=204)
def delete_user(user_id: int):
    success = user_service.delete_user(user_id)
    if not success:
        raise HTTPException(status_code=404, detail="User not found")

@router.put("", response_model=UserRead)
def update_user(user_update: UserUpdate):
    user = user_service.update_user(user_update.id, user_update)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user


