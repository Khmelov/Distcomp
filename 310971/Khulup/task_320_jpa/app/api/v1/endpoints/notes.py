from fastapi import APIRouter, HTTPException, Query
from app.schemas.note import NoteCreate, NoteRead, NoteUpdate
from app.models.note import Note
from app.repositories.note_repository import NoteRepository
from app.services.note_service import NoteService
from app.core.db import SessionLocal
import logging

router = APIRouter()

@router.post("", response_model=NoteRead, status_code=201)
def create_note(note: NoteCreate):
    session = SessionLocal()
    try:
        repo = NoteRepository(session)
        service = NoteService(repo)
        created = service.create_note(note)
        session.expunge_all()
        return created
    except ValueError as e:
        if "does not exist" in str(e):
            raise HTTPException(status_code=400, detail="Issue with this id does not exist")
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        logging.error(f"Error creating note: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.get("/{note_id}", response_model=NoteRead)
def get_note(note_id: int):
    session = SessionLocal()
    try:
        repo = NoteRepository(session)
        service = NoteService(repo)
        note = service.get_note(note_id)
        if not note:
            raise HTTPException(status_code=404, detail="Note not found")
        return note
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error getting note {note_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.get("", response_model=list[NoteRead])
def list_notes(
    issueId: int | None = Query(None, ge=1),
    limit: int = Query(50, ge=0, le=1000),
    offset: int = Query(0, ge=0),
    sort_by: str | None = Query(None, pattern="^(id|issueId)$"),
    sort_dir: str = Query("desc", pattern="^(asc|desc)$"),
):
    session = SessionLocal()
    try:
        repo = NoteRepository(session)
        service = NoteService(repo)
        return service.list_notes(
            issueId=issueId, limit=limit, offset=offset, sort_by=sort_by, sort_dir=sort_dir
        )
    except Exception as e:
        logging.error(f"Error listing notes: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.delete("/{note_id}", status_code=204)
def delete_note(note_id: int):
    session = SessionLocal()
    try:
        repo = NoteRepository(session)
        service = NoteService(repo)
        success = service.delete_note(note_id)
        if not success:
            raise HTTPException(status_code=404, detail="Note not found")
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error deleting note {note_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.put("", response_model=NoteRead)
def update_note_body(note_data: NoteUpdate):
    session = SessionLocal()
    try:
        repo = NoteRepository(session)
        service = NoteService(repo)
        updated = service.update_note(note_data)
        if not updated:
            raise HTTPException(status_code=404, detail="Note not found")
        session.expunge_all()
        return updated
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating note {note_data.id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.put("/{note_id}", response_model=NoteRead)
def update_note(note_id: int, note_data: NoteCreate):
    session = SessionLocal()
    try:
        repo = NoteRepository(session)
        service = NoteService(repo)
        note_to_update = Note(id=note_id, issueId=note_data.issueId, content=note_data.content)
        updated = service.update_note(note_to_update)
        if not updated:
            raise HTTPException(status_code=404, detail="Note not found")
        session.expunge_all()
        return updated
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating note {note_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()
