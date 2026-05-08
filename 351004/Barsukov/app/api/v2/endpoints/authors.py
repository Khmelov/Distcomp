from fastapi import APIRouter, status, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from schemas.author import AuthorRequestTo, AuthorResponseTo
from services.author_service import AuthorService
from api.v2.deps import get_current_user
from typing import List
import models

router = APIRouter()
service = AuthorService()


# РЕГИСТРАЦИЯ: Доступна всем (без Depends(get_current_user))
@router.post("", response_model=AuthorResponseTo, status_code=status.HTTP_201_CREATED)
async def register(dto: AuthorRequestTo, db: Session = Depends(get_db)):
    # Проверка на уникальность логина
    existing = db.query(models.Author).filter(models.Author.login == dto.login).first()
    if existing:
        raise HTTPException(status_code=400, detail="Login already registered", headers={"errorCode": "40001"})

    return service.create(db, dto)


# ПОЛУЧЕНИЕ СПИСКА: Только чтение
@router.get("", response_model=List[AuthorResponseTo])
async def get_all(skip: int = 0, limit: int = 10, db: Session = Depends(get_db),
                  current_user=Depends(get_current_user)):
    return service.get_all(db, skip, limit)


# ПОЛУЧЕНИЕ ПРОФИЛЯ
@router.get("/{id}", response_model=AuthorResponseTo)
async def get_by_id(id: int, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    res = await service.get_by_id(db, id)
    if not res:
        raise HTTPException(status_code=404, detail="Author not found", headers={"errorCode": "40401"})
    return res


# УДАЛЕНИЕ: Только свой профиль или ADMIN
@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, db: Session = Depends(get_db), current_user: models.Author = Depends(get_current_user)):
    if current_user.role != "ADMIN" and current_user.id != id:
        raise HTTPException(status_code=403, detail="Forbidden: You can only delete your own profile",
                            headers={"errorCode": "40301"})

    if not await service.delete(db, id):
        raise HTTPException(status_code=404, detail="Author not found")