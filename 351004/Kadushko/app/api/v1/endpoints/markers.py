from fastapi import APIRouter, Depends, status
from typing import List
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.marker import MarkerCreate, MarkerUpdate, MarkerResponse
from app.services.marker_service import MarkerService

router = APIRouter(prefix="/markers", tags=["markers"])


@router.get("", response_model=List[MarkerResponse], status_code=status.HTTP_200_OK)
def get_markers(
    page: int = 0,
    size: int = 10000,
    sort_by: str = "id",
    sort_order: str = "asc",
    db: Session = Depends(get_db)
):
    return MarkerService(db).get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)


@router.get("/{marker_id}", response_model=MarkerResponse, status_code=status.HTTP_200_OK)
def get_marker(marker_id: int, db: Session = Depends(get_db)):
    return MarkerService(db).get_by_id(marker_id)


@router.post("", response_model=MarkerResponse, status_code=status.HTTP_201_CREATED)
def create_marker(data: MarkerCreate, db: Session = Depends(get_db)):
    return MarkerService(db).create(data)


@router.put("/{marker_id}", response_model=MarkerResponse, status_code=status.HTTP_200_OK)
def update_marker(marker_id: int, data: MarkerUpdate, db: Session = Depends(get_db)):
    data.id = marker_id
    return MarkerService(db).update(data)


@router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_marker(marker_id: int, db: Session = Depends(get_db)):
    MarkerService(db).delete(marker_id)