# app/api/v1/endpoints/authors.py
from fastapi import APIRouter, status, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from schemas.author import AuthorRequestTo, AuthorResponseTo
from services.author_service import AuthorService
from typing import List

router = APIRouter()
service = AuthorService()

@router.post("", response_model=AuthorResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: AuthorRequestTo, db: Session = Depends(get_db)):
    return service.create(db, dto)

@router.get("", response_model=List[AuthorResponseTo])
async def get_all(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return service.get_all(db, skip, limit)

@router.get("/{id}", response_model=AuthorResponseTo)
async def get_by_id(id: int, db: Session = Depends(get_db)):
    # ТЕПЕРЬ ТУТ AWAIT
    res = await service.get_by_id(db, id)
    if not res:
        raise HTTPException(status_code=404, detail="Author not found")
    return res

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, db: Session = Depends(get_db)):
    # ТЕПЕРЬ ТУТ AWAIT
    if not await service.delete(db, id):
        raise HTTPException(status_code=404)