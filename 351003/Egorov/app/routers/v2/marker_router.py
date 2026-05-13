from typing import List

from fastapi import APIRouter, Depends, HTTPException, status

from app.auth.dependencies import get_current_user
from app.dtos.marker_request import MarkerRequestTo
from app.dtos.marker_response import MarkerResponseTo
from app.models.creator import Creator, CreatorRole
from app.services.marker_service import MarkerService


router = APIRouter(prefix="/api/v2.0/markers", tags=["v2-markers"])


def get_marker_service() -> MarkerService:
    from main import marker_service

    return marker_service


def _ensure_admin(user: Creator) -> None:
    if user.role != CreatorRole.ADMIN:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")


@router.post("", response_model=MarkerResponseTo, status_code=status.HTTP_201_CREATED)
def create_marker(
    dto: MarkerRequestTo,
    service: MarkerService = Depends(get_marker_service),
    current_user: Creator = Depends(get_current_user),
) -> MarkerResponseTo:
    _ensure_admin(current_user)
    return service.create_marker(dto)


@router.get("", response_model=List[MarkerResponseTo])
def list_markers(
    service: MarkerService = Depends(get_marker_service),
    _: Creator = Depends(get_current_user),
) -> List[MarkerResponseTo]:
    return service.get_all_markers()


@router.get("/{marker_id}", response_model=MarkerResponseTo)
def get_marker(
    marker_id: int,
    service: MarkerService = Depends(get_marker_service),
    _: Creator = Depends(get_current_user),
) -> MarkerResponseTo:
    marker = service.get_marker(marker_id)
    if not marker:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Marker not found")
    return marker


@router.put("/{marker_id}", response_model=MarkerResponseTo)
def update_marker(
    marker_id: int,
    dto: MarkerRequestTo,
    service: MarkerService = Depends(get_marker_service),
    current_user: Creator = Depends(get_current_user),
) -> MarkerResponseTo:
    _ensure_admin(current_user)
    updated = service.update_marker(marker_id, dto)
    if not updated:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Marker not found")
    return updated


@router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_marker(
    marker_id: int,
    service: MarkerService = Depends(get_marker_service),
    current_user: Creator = Depends(get_current_user),
) -> None:
    _ensure_admin(current_user)
    deleted = service.delete_marker(marker_id)
    if not deleted:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Marker not found")
