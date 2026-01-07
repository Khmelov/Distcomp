from fastapi import APIRouter, HTTPException
from app.schemas.marker import MarkerCreate, MarkerRead, MarkerUpdate
from app.services.marker_service import MarkerService
from app.repositories.marker_repository import MarkerRepository

router = APIRouter()
repo = MarkerRepository()
service = MarkerService(repo)

@router.post("", response_model=MarkerRead, status_code=201)
def create_marker(marker: MarkerCreate):
    return service.create(marker)

@router.get("/{marker_id}", response_model=MarkerRead)
def get_marker(marker_id: int):
    marker = service.get(marker_id)
    if not marker:
        raise HTTPException(status_code=404, detail="Marker not found")
    return marker

@router.get("", response_model=list[MarkerRead])
def list_markers():
    return service.list()

@router.put("", response_model=MarkerRead)
def update_marker(marker: MarkerUpdate):
    updated = service.update(marker)
    if not updated:
        raise HTTPException(status_code=404, detail="Marker not found")
    return updated

@router.delete("/{marker_id}", status_code=204)
def delete_marker(marker_id: int):
    if not service.delete(marker_id):
        raise HTTPException(status_code=404, detail="Marker not found")
