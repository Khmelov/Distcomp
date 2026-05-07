from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.api.v1.dep import get_marker_service
from src.schemas.marker import MarkerResponseTo, MarkerRequestTo
from src.services.marker import MarkerService

router = APIRouter(prefix="/markers")

@router.get("", response_model=List[MarkerResponseTo], status_code=HTTPStatus.OK)
def get_all(service: MarkerService = Depends(get_marker_service)):
    return service.get_all()

@router.get("/{marker_id}", response_model=MarkerResponseTo, status_code=HTTPStatus.OK)
def get_by_id(marker_id: str, service: MarkerService = Depends(get_marker_service)):
    return service.get_one(marker_id)

@router.post("", response_model=MarkerResponseTo, status_code=HTTPStatus.CREATED)
def create(dto: MarkerRequestTo, service: MarkerService = Depends(get_marker_service)):
    return service.create(dto)

@router.put("/{marker_id}", response_model=MarkerResponseTo, status_code=HTTPStatus.OK)
def update(marker_id: str, dto: MarkerRequestTo, service: MarkerService = Depends(get_marker_service)):
    return service.update(marker_id, dto)

@router.delete("/{marker_id}", status_code=HTTPStatus.NO_CONTENT)
def delete(marker_id: str, service: MarkerService = Depends(get_marker_service)):
    service.delete(marker_id)