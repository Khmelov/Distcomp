from fastapi import APIRouter, Depends, status
from typing import List
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.editor import EditorCreate, EditorUpdate, EditorResponse
from app.services.editor_service import EditorService

router = APIRouter(prefix="/editors", tags=["editors"])


@router.get("", response_model=List[EditorResponse], status_code=status.HTTP_200_OK)
def get_editors(
    page: int = 0,
    size: int = 10000,
    sort_by: str = "id",
    sort_order: str = "asc",
    db: Session = Depends(get_db)
):
    return EditorService(db).get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)


@router.get("/{editor_id}", response_model=EditorResponse, status_code=status.HTTP_200_OK)
def get_editor(editor_id: int, db: Session = Depends(get_db)):
    return EditorService(db).get_by_id(editor_id)


@router.post("", response_model=EditorResponse, status_code=status.HTTP_201_CREATED)
def create_editor(data: EditorCreate, db: Session = Depends(get_db)):
    return EditorService(db).create(data)


@router.put("/{editor_id}", response_model=EditorResponse, status_code=status.HTTP_200_OK)
def update_editor(editor_id: int, data: EditorUpdate, db: Session = Depends(get_db)):
    data.id = editor_id
    return EditorService(db).update(data)


@router.delete("/{editor_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_editor(editor_id: int, db: Session = Depends(get_db)):
    EditorService(db).delete(editor_id)