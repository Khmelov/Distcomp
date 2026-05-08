from fastapi import APIRouter, Depends, status
from fastapi.responses import JSONResponse
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from typing import List
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.editor import EditorCreate, EditorUpdate, EditorResponse
from app.services.editor_service import EditorService
from app.auth import get_current_user, security

router = APIRouter(prefix="/editors", tags=["editors-v2"])


@router.get("", response_model=List[EditorResponse])
def get_editors(
    credentials: HTTPAuthorizationCredentials = Depends(security),
    db: Session = Depends(get_db)
):
    get_current_user(credentials)
    return EditorService(db).get_all()


@router.get("/{editor_id}", response_model=EditorResponse)
def get_editor(
    editor_id: int,
    credentials: HTTPAuthorizationCredentials = Depends(security),
    db: Session = Depends(get_db)
):
    get_current_user(credentials)
    return EditorService(db).get_by_id(editor_id)


@router.put("/{editor_id}", response_model=EditorResponse)
def update_editor(
    editor_id: int,
    data: EditorUpdate,
    credentials: HTTPAuthorizationCredentials = Depends(security),
    db: Session = Depends(get_db)
):
    get_current_user(credentials)
    data.id = editor_id
    return EditorService(db).update(data)


@router.delete("/{editor_id}", status_code=204)
def delete_editor(
    editor_id: int,
    credentials: HTTPAuthorizationCredentials = Depends(security),
    db: Session = Depends(get_db)
):
    get_current_user(credentials)
    EditorService(db).delete(editor_id)
