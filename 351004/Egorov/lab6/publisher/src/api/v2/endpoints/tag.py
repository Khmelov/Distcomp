from http import HTTPStatus
from typing import List

from fastapi import APIRouter
from fastapi.params import Depends

from src.deps.base import get_tag_service
from src.schemas.tag import TagResponseTo, TagRequestTo
from src.services import TagService

router = APIRouter(prefix="/tags")

@router.get("", response_model=List[TagResponseTo], status_code=HTTPStatus.OK)
async def get_all(service: TagService = Depends(get_tag_service)):
    return await service.get_all()

@router.get("/{tag_id}", response_model=TagResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(tag_id: int, service: TagService = Depends(get_tag_service)):
    return await service.get_one(tag_id)

@router.post("", response_model=TagResponseTo, status_code=HTTPStatus.CREATED)
async def create(dto: TagRequestTo, service: TagService = Depends(get_tag_service)):
    return await service.create(dto)

@router.put("/{tag_id}", response_model=TagResponseTo, status_code=HTTPStatus.OK)
async def update(tag_id: int, dto: TagRequestTo, service: TagService = Depends(get_tag_service)):
    return await service.update(tag_id, dto)

@router.delete("/{tag_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(tag_id: int, service: TagService = Depends(get_tag_service)):
    await service.delete(tag_id)