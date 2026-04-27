from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.deps import get_author_service
from src.schemas.author import AuthorResponseTo, AuthorRequestTo
from src.services.author import AuthorService

router = APIRouter(prefix="/authors")

@router.get("", response_model=List[AuthorResponseTo], status_code=HTTPStatus.OK)
async def get_all(service: AuthorService = Depends(get_author_service)):
    return await service.get_all()

@router.get("/{author_id}", response_model=AuthorResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(author_id: int, service: AuthorService = Depends(get_author_service)):
    return await service.get_one(author_id)

@router.post("", response_model=AuthorResponseTo, status_code=HTTPStatus.CREATED)
async def create(request: AuthorRequestTo, service: AuthorService = Depends(get_author_service)):
    return await service.create(request)


@router.put("/{author_id}", response_model=AuthorResponseTo, status_code=HTTPStatus.OK)
async def update(author_id: int, dto: AuthorRequestTo, service: AuthorService = Depends(get_author_service)):
    return await service.update(author_id, dto)

@router.delete("/{author_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(author_id: int, service: AuthorService = Depends(get_author_service)):
    await service.delete(author_id)