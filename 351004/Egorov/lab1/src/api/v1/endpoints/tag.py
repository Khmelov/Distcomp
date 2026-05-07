from http import HTTPStatus
from typing import List

from fastapi import APIRouter
from fastapi.params import Depends

from src.api.v1.dep import get_tag_service
from src.schemas.tag import TagResponseTo, TagRequestTo
from src.services import TagService

router = APIRouter(prefix="/tags")

@router.get("", response_model=List[TagResponseTo], status_code=HTTPStatus.OK)
def get_all(service: TagService = Depends(get_tag_service)):
    return service.get_all()

@router.get("/{tag_id}", response_model=TagResponseTo, status_code=HTTPStatus.OK)
def get_by_id(tag_id: int, service: TagService = Depends(get_tag_service)):
    return service.get_one(tag_id)

@router.post("", response_model=TagResponseTo, status_code=HTTPStatus.CREATED)
def create(dto: TagRequestTo, service: TagService = Depends(get_tag_service)):
    return service.create(dto)

@router.put("/{tag_id}", response_model=TagResponseTo, status_code=HTTPStatus.OK)
def update(tag_id: int, dto: TagRequestTo, service: TagService = Depends(get_tag_service)):
    return service.update(tag_id, dto)

@router.delete("/{tag_id}", status_code=HTTPStatus.NO_CONTENT)
def delete(tag_id: int, service: TagService = Depends(get_tag_service)):
    service.delete(tag_id)