from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.api.v1.dep import get_author_service
from src.schemas.author import AuthorResponseTo, AuthorRequestTo
from src.services.author import AuthorService

router = APIRouter(prefix="/authors")

@router.get("", response_model=List[AuthorResponseTo], status_code=HTTPStatus.OK)
def get_all(service: AuthorService = Depends(get_author_service)):
    return service.get_all()

@router.get("/{author_id}", response_model=AuthorResponseTo, status_code=HTTPStatus.OK)
def get_by_id(author_id: int, service: AuthorService = Depends(get_author_service)):
    return service.get_one(author_id)

@router.post("", response_model=AuthorResponseTo, status_code=HTTPStatus.CREATED)
def create(request: AuthorRequestTo, service: AuthorService = Depends(get_author_service)):
    return service.create(request)


@router.put("/{author_id}", response_model=AuthorResponseTo, status_code=HTTPStatus.OK)
def update(author_id: int, dto: AuthorRequestTo, service: AuthorService = Depends(get_author_service)):
    return service.update(author_id, dto)

@router.delete("/{author_id}", status_code=HTTPStatus.NO_CONTENT)
def delete(author_id: int, service: AuthorService = Depends(get_author_service)):
    service.delete(author_id)