from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.api.v1.dep import get_creator_service
from src.schemas.creator import CreatorResponseTo, CreatorRequestTo
from src.services.creator import CreatorService

router = APIRouter(prefix="/creators")

@router.get("", response_model=List[CreatorResponseTo], status_code=HTTPStatus.OK)
def get_all(service: CreatorService = Depends(get_creator_service)):
    return service.get_all()

@router.get("/{creator_id}", response_model=CreatorResponseTo, status_code=HTTPStatus.OK)
def get_by_id(creator_id: str, service: CreatorService = Depends(get_creator_service)):
    return service.get_one(creator_id)

@router.post("", response_model=CreatorResponseTo, status_code=HTTPStatus.CREATED)
def create(dto: CreatorRequestTo, service: CreatorService = Depends(get_creator_service)):
    return service.create(dto)

@router.put("/{creator_id}", response_model=CreatorResponseTo, status_code=HTTPStatus.OK)
def update(creator_id: str, dto: CreatorRequestTo, service: CreatorService = Depends(get_creator_service)):
    return service.update(creator_id, dto)

@router.delete("/{creator_id}", status_code=HTTPStatus.NO_CONTENT)
def delete(creator_id: str, service: CreatorService = Depends(get_creator_service)):
    service.delete(creator_id)