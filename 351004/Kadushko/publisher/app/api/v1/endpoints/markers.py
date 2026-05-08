from fastapi import APIRouter, Depends, status
from typing import List
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.marker import MarkerCreate, MarkerUpdate, MarkerResponse
from app.services.marker_service import MarkerService
from app import cache

router = APIRouter(prefix="/markers", tags=["markers"])


@router.get("", response_model=List[MarkerResponse], status_code=status.HTTP_200_OK)
def get_markers(
    page: int = 0,
    size: int = 10000,
    sort_by: str = "id",
    sort_order: str = "asc",
    db: Session = Depends(get_db)
):
    cache_key = f"markers:all:{page}:{size}:{sort_by}:{sort_order}"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return cached
    result = MarkerService(db).get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)
    data = [r.model_dump(by_alias=True) for r in result]
    cache.cache_set(cache_key, data)
    return data


@router.get("/{marker_id}", response_model=MarkerResponse, status_code=status.HTTP_200_OK)
def get_marker(marker_id: int, db: Session = Depends(get_db)):
    cache_key = f"markers:{marker_id}"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return cached
    result = MarkerService(db).get_by_id(marker_id)
    data = result.model_dump(by_alias=True)
    cache.cache_set(cache_key, data)
    return data


@router.post("", response_model=MarkerResponse, status_code=status.HTTP_201_CREATED)
def create_marker(data: MarkerCreate, db: Session = Depends(get_db)):
    result = MarkerService(db).create(data)
    data_out = result.model_dump(by_alias=True)
    cache.cache_set(f"markers:{result.id}", data_out)
    cache.cache_delete_pattern("markers:all:*")
    return data_out


@router.put("/{marker_id}", response_model=MarkerResponse, status_code=status.HTTP_200_OK)
def update_marker(marker_id: int, data: MarkerUpdate, db: Session = Depends(get_db)):
    data.id = marker_id
    result = MarkerService(db).update(data)
    data_out = result.model_dump(by_alias=True)
    cache.cache_set(f"markers:{marker_id}", data_out)
    cache.cache_delete_pattern("markers:all:*")
    return data_out


@router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_marker(marker_id: int, db: Session = Depends(get_db)):
    MarkerService(db).delete(marker_id)
    cache.cache_delete(f"markers:{marker_id}")
    cache.cache_delete_pattern("markers:all:*")
