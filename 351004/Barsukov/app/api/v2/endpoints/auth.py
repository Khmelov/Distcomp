from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm  # Важно!
from sqlalchemy.orm import Session
from database import get_db
from core.security import verify_password, create_access_token
import models

router = APIRouter()


@router.post("/login")
async def login(
        db: Session = Depends(get_db),
        form_data: OAuth2PasswordRequestForm = Depends()  # Считывает username и password из формы
):
    # 1. Ищем пользователя по логину (Swagger шлет его в поле username)
    user = db.query(models.Author).filter(models.Author.login == form_data.username).first()

    # 2. Проверяем существование и пароль
    if not user or not verify_password(form_data.password, user.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid login or password",
            headers={"errorCode": "40101"}
        )

    # 3. Генерируем токен
    access_token = create_access_token(data={"sub": user.login, "role": user.role})


    return {
        "access_token": access_token,
        "token_type": "bearer"
    }