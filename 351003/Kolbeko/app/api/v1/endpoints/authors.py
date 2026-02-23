from fastapi import APIRouter, status
from app.schemas.author import AuthorRequestTo, AuthorResponseTo
from app.services.author_service import AuthorService
from typing import List

router = APIRouter()
service = AuthorService()

@router.post("", response_model=AuthorResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: AuthorRequestTo):
    return service.create(dto)

@router.get("", response_model=List[AuthorResponseTo])
async def get_all():
    return service.get_all()

@router.get("/{id}", response_model=AuthorResponseTo)
async def get_by_id(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=AuthorResponseTo)
async def update(id: int, dto: AuthorRequestTo):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int):
    service.delete(id)