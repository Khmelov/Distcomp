from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.api.v1.dep import get_post_service
from src.schemas.post import PostResponseTo, PostRequestTo
from src.services.post import PostService

router = APIRouter(prefix="/posts")

@router.get("", response_model=List[PostResponseTo], status_code=HTTPStatus.OK)
def get_all(service: PostService = Depends(get_post_service)):
    return service.get_all()

@router.get("/{post_id}", response_model=PostResponseTo, status_code=HTTPStatus.OK)
def get_by_id(post_id: str, service: PostService = Depends(get_post_service)):
    return service.get_one(post_id)

@router.post("", response_model=PostResponseTo, status_code=HTTPStatus.CREATED)
def create(dto: PostRequestTo, service: PostService = Depends(get_post_service)):
    return service.create(dto)

@router.put("/{post_id}", response_model=PostResponseTo, status_code=HTTPStatus.OK)
def update(post_id: str, dto: PostRequestTo, service: PostService = Depends(get_post_service)):
    return service.update(post_id, dto)

@router.delete("/{post_id}", status_code=HTTPStatus.NO_CONTENT)
def delete(post_id: str, service: PostService = Depends(get_post_service)):
    service.delete(post_id)