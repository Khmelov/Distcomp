from typing import List

from fastapi import APIRouter, Depends, HTTPException, status

from app.dtos.author_request import AuthorRequestTo
from app.dtos.author_response import AuthorResponseTo
from app.services.author_service import AuthorService

router = APIRouter(prefix="/api/v1.0/authors", tags=["authors"])


def get_author_service() -> AuthorService:
    from main import author_service
    return author_service


@router.post("", response_model=AuthorResponseTo, status_code=status.HTTP_201_CREATED)
def create_author(
    dto: AuthorRequestTo,
    service: AuthorService = Depends(get_author_service),
) -> AuthorResponseTo:
    return service.create_author(dto)


@router.get("", response_model=List[AuthorResponseTo])
def list_authors(
    service: AuthorService = Depends(get_author_service),
) -> List[AuthorResponseTo]:
    return service.get_all_authors()


@router.get("/{author_id}", response_model=AuthorResponseTo)
def get_author(
    author_id: int,
    service: AuthorService = Depends(get_author_service),
) -> AuthorResponseTo:
    author = service.get_author(author_id)
    if not author:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Author not found", "errorCode": 40401},
        )
    return author


@router.put("/{author_id}", response_model=AuthorResponseTo)
def update_author(
    author_id: int,
    dto: AuthorRequestTo,
    service: AuthorService = Depends(get_author_service),
) -> AuthorResponseTo:
    updated = service.update_author(author_id, dto)
    if not updated:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Author not found", "errorCode": 40401},
        )
    return updated


@router.delete("/{author_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_author(
    author_id: int,
    service: AuthorService = Depends(get_author_service),
) -> None:
    deleted = service.delete_author(author_id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Author not found", "errorCode": 40401},
        )