from fastapi import APIRouter, Depends, HTTPException, status
from typing import List
from dto.requests import MarkerRequestTo
from dto.responses import MarkerResponseTo
from services.marker import MarkerService
from dependencies import get_marker_service

router = APIRouter(prefix="/markers", tags=["markers"])


@router.get("", response_model=List[MarkerResponseTo])
def get_all_markers(service: MarkerService = Depends(get_marker_service)):
    return service.get_all()


@router.get("/{id}", response_model=MarkerResponseTo)
def get_marker(id: int, service: MarkerService = Depends(get_marker_service)):
    marker = service.get(id)
    if not marker:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Marker not found"
        )
    return marker


@router.post("", response_model=MarkerResponseTo, status_code=status.HTTP_201_CREATED)
def create_marker(
    request: MarkerRequestTo, service: MarkerService = Depends(get_marker_service)
):
    return service.create(request)


@router.put("/{id}", response_model=MarkerResponseTo)
def update_marker(
    id: int,
    request: MarkerRequestTo,
    service: MarkerService = Depends(get_marker_service),
):
    try:
        return service.update(id, request)
    except ValueError as e:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(e))


@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_marker(id: int, service: MarkerService = Depends(get_marker_service)):
    if not service.delete(id):
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Marker not found"
        )
