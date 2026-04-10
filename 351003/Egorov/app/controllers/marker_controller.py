from typing import List

from fastapi import APIRouter, Depends, HTTPException, status

from app.dtos.marker_request import MarkerRequestTo
from app.dtos.marker_response import MarkerResponseTo
from app.services.marker_service import MarkerService


router = APIRouter(prefix="/api/v1.0/markers", tags=["markers"])


def get_marker_service() -> MarkerService:
    from main import marker_service

    return marker_service


@router.post("", response_model=MarkerResponseTo, status_code=status.HTTP_201_CREATED)
def create_marker(dto: MarkerRequestTo, service: MarkerService = Depends(get_marker_service)) -> MarkerResponseTo:
    return service.create_marker(dto)


@router.get("", response_model=List[MarkerResponseTo])
def list_markers(service: MarkerService = Depends(get_marker_service)) -> List[MarkerResponseTo]:
    return service.get_all_markers()


@router.get("/{marker_id}", response_model=MarkerResponseTo)
def get_marker(marker_id: int, service: MarkerService = Depends(get_marker_service)) -> MarkerResponseTo:
    marker = service.get_marker(marker_id)
    if not marker:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Marker not found", "errorCode": 40401},
        )
    return marker


@router.put("/{marker_id}", response_model=MarkerResponseTo)
def update_marker(
    marker_id: int, dto: MarkerRequestTo, service: MarkerService = Depends(get_marker_service)
) -> MarkerResponseTo:
    updated = service.update_marker(marker_id, dto)
    if not updated:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Marker not found", "errorCode": 40401},
        )
    return updated


@router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_marker(marker_id: int, service: MarkerService = Depends(get_marker_service)) -> None:
    deleted = service.delete_marker(marker_id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Marker not found", "errorCode": 40401},
        )

