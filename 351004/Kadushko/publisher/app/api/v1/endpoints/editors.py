from fastapi import APIRouter, Depends, status
from typing import List
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.editor import EditorCreate, EditorUpdate, EditorResponse
from app.services.editor_service import EditorService
from app import cache

router = APIRouter(prefix="/editors", tags=["editors"])


@router.get("", response_model=List[EditorResponse], status_code=status.HTTP_200_OK)
def get_editors(
    page: int = 0,
    size: int = 10000,
    sort_by: str = "id",
    sort_order: str = "asc",
    db: Session = Depends(get_db)
):
    cache_key = f"editors:all:{page}:{size}:{sort_by}:{sort_order}"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return cached
    result = EditorService(db).get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)
    data = [r.model_dump(by_alias=True) for r in result]
    cache.cache_set(cache_key, data)
    return data


@router.get("/{editor_id}", response_model=EditorResponse, status_code=status.HTTP_200_OK)
def get_editor(editor_id: int, db: Session = Depends(get_db)):
    cache_key = f"editors:{editor_id}"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return cached
    result = EditorService(db).get_by_id(editor_id)
    data = result.model_dump(by_alias=True)
    cache.cache_set(cache_key, data)
    return data


@router.post("", response_model=EditorResponse, status_code=status.HTTP_201_CREATED)
def create_editor(data: EditorCreate, db: Session = Depends(get_db)):
    result = EditorService(db).create(data)
    data_out = result.model_dump(by_alias=True)
    cache.cache_set(f"editors:{result.id}", data_out)
    cache.cache_delete_pattern("editors:all:*")
    return data_out


@router.put("/{editor_id}", response_model=EditorResponse, status_code=status.HTTP_200_OK)
def update_editor(editor_id: int, data: EditorUpdate, db: Session = Depends(get_db)):
    data.id = editor_id
    result = EditorService(db).update(data)
    data_out = result.model_dump(by_alias=True)
    cache.cache_set(f"editors:{editor_id}", data_out)
    cache.cache_delete_pattern("editors:all:*")
    return data_out


@router.delete("/{editor_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_editor(editor_id: int, db: Session = Depends(get_db)):
    EditorService(db).delete(editor_id)
    cache.cache_delete(f"editors:{editor_id}")
    cache.cache_delete_pattern("editors:all:*")