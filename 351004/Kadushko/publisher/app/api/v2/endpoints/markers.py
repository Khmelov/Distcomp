from fastapi import APIRouter, Depends
from fastapi.responses import JSONResponse
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from typing import List
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.marker import MarkerCreate, MarkerUpdate, MarkerResponse
from app.services.marker_service import MarkerService
from app.auth import get_current_user, security

router = APIRouter(prefix="/markers", tags=["markers-v2"])


@router.get("")
def get_markers(credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    return MarkerService(db).get_all()


@router.get("/{marker_id}")
def get_marker(marker_id: int, credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    return MarkerService(db).get_by_id(marker_id)


@router.post("", status_code=201)
def create_marker(data: MarkerCreate, credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    return MarkerService(db).create(data)


@router.put("/{marker_id}")
def update_marker(marker_id: int, data: MarkerUpdate, credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    data.id = marker_id
    return MarkerService(db).update(data)


@router.delete("/{marker_id}", status_code=204)
def delete_marker(marker_id: int, credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    MarkerService(db).delete(marker_id)
