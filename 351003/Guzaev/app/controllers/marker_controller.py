from fastapi import APIRouter, status, Response
from typing import List
from app.dtos.marker_dto import MarkerRequestTo, MarkerResponseTo
from app.services.marker_service import MarkerService

router = APIRouter(prefix="/api/v1.0/markers", tags=["markers"])
service = MarkerService()

@router.post("", status_code=status.HTTP_201_CREATED, response_model=MarkerResponseTo)
def create_marker(dto: MarkerRequestTo):
    return service.create(dto)

@router.get("", response_model=List[MarkerResponseTo])
def get_markers():
    return service.get_all()

@router.get("/{id}", response_model=MarkerResponseTo)
def get_marker(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=MarkerResponseTo)
def update_marker(id: int, dto: MarkerRequestTo):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_marker(id: int):
    service.delete(id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
