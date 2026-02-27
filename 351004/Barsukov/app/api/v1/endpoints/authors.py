from fastapi import APIRouter, status, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from app.schemas.author import AuthorRequestTo, AuthorResponseTo
from app.services.author_service import AuthorService
from typing import List

router = APIRouter()
service = AuthorService()

@router.post("", response_model=AuthorResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: AuthorRequestTo, db: Session = Depends(get_db)):
    return service.create(db, dto)

@router.get("", response_model=List[AuthorResponseTo])
async def get_all(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return service.get_all(db, skip, limit)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, db: Session = Depends(get_db)):
    if not service.delete(db, id):
        raise HTTPException(status_code=404)