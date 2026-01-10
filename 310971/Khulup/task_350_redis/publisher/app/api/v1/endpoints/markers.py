from fastapi import APIRouter, HTTPException, Query
from app.schemas.marker import MarkerCreate, MarkerRead, MarkerUpdate
from app.services.marker_service import MarkerService
from app.repositories.marker_repository import MarkerRepository
from app.core.db import SessionLocal
import logging

router = APIRouter()

@router.post("", response_model=MarkerRead, status_code=201)
def create_marker(marker: MarkerCreate):
    session = SessionLocal()
    try:
        repo = MarkerRepository(session)
        service = MarkerService(repo)
        created = service.create(marker)
        session.expunge_all()
        return created
    except Exception as e:
        logging.error(f"Error creating marker: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.get("/{marker_id}", response_model=MarkerRead)
def get_marker(marker_id: int):
    session = SessionLocal()
    try:
        repo = MarkerRepository(session)
        service = MarkerService(repo)
        marker = service.get(marker_id)
        if not marker:
            raise HTTPException(status_code=404, detail="Marker not found")
        return marker
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error getting marker {marker_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.get("", response_model=list[MarkerRead])
def list_markers(
    name: str | None = None,
    limit: int = Query(50, ge=0, le=1000),
    offset: int = Query(0, ge=0),
    sort_by: str | None = Query(None, pattern="^(id|name)$"),
    sort_dir: str = Query("desc", pattern="^(asc|desc)$"),
):
    session = SessionLocal()
    try:
        repo = MarkerRepository(session)
        service = MarkerService(repo)
        return service.list(
            name=name, limit=limit, offset=offset, sort_by=sort_by, sort_dir=sort_dir
        )
    except Exception as e:
        logging.error(f"Error listing markers: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.put("", response_model=MarkerRead)
def update_marker_body(marker: MarkerUpdate):
    session = SessionLocal()
    try:
        repo = MarkerRepository(session)
        service = MarkerService(repo)
        updated = service.update(marker)
        if not updated:
            raise HTTPException(status_code=404, detail="Marker not found")
        session.expunge_all()
        return updated
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating marker {marker.id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.put("/{marker_id}", response_model=MarkerRead)
def update_marker(marker_id: int, marker: MarkerCreate):
    session = SessionLocal()
    try:
        repo = MarkerRepository(session)
        service = MarkerService(repo)
        update_data = MarkerUpdate(id=marker_id, name=marker.name)
        updated = service.update(update_data)
        if not updated:
            raise HTTPException(status_code=404, detail="Marker not found")
        session.expunge_all()
        return updated
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating marker {marker_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.delete("/{marker_id}", status_code=204)
def delete_marker(marker_id: int):
    session = SessionLocal()
    try:
        repo = MarkerRepository(session)
        service = MarkerService(repo)
        if not service.delete(marker_id):
            raise HTTPException(status_code=404, detail="Marker not found")
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error deleting marker {marker_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()
