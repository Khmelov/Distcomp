from fastapi import APIRouter, HTTPException
from app.models import UserRequestTo, UserResponseTo
from app.storage import users
import uuid

router = APIRouter(prefix="/api/v1.0/users", tags=["Users"])

# Создание пользователя
@router.post("/", response_model=UserResponseTo, status_code=201)
def create_user(user: UserRequestTo):
    # Генерация уникального ID для нового пользователя
    user_id = str(uuid.uuid4())
    # Создание нового пользователя с этим ID
    new_user = UserResponseTo(id=user_id, **user.dict())
    # Сохранение пользователя в хранилище
    users[user_id] = new_user
    return new_user

# Получение пользователя по ID
@router.get("/{user_id}", response_model=UserResponseTo)
def get_user(user_id: str):
    # Проверка, существует ли пользователь с таким ID
    if user_id not in users:
        raise HTTPException(status_code=404, detail="User not found")
    return users[user_id]

# Удаление пользователя по ID
@router.delete("/{user_id}", status_code=204)
def delete_user(user_id: str):
    # Проверка, существует ли пользователь с таким ID
    if user_id not in users:
        raise HTTPException(status_code=404, detail="User not found")
    # Удаление пользователя
    del users[user_id]
