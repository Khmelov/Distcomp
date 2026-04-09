from fastapi import APIRouter, Response, status

from ..dto.author_dto import AuthorRequestTo, AuthorResponseTo
from ..services.author_service import AuthorService

router = APIRouter(prefix="/api/v1.0/authors", tags=["author"])
service = AuthorService()


@router.post("", response_model=AuthorResponseTo, status_code=status.HTTP_201_CREATED)
def create_author(dto: AuthorRequestTo):
    return service.create(dto)


@router.get("", response_model=list[AuthorResponseTo], status_code=status.HTTP_200_OK)
def get_authors():
    return service.get_all()


@router.get("/{author_id}", response_model=AuthorResponseTo, status_code=status.HTTP_200_OK)
def get_author(author_id: int):
    return service.get_by_id(author_id)


@router.put("/{author_id}", response_model=AuthorResponseTo, status_code=status.HTTP_200_OK)
def update_author(author_id: int, dto: AuthorRequestTo):
    return service.update(author_id, dto)


@router.delete("/{author_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_author(author_id: int):
    service.delete(author_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)