from fastapi import APIRouter, HTTPException, Query, status, Request
from typing import List, Optional
import json
import logging
from pydantic import ValidationError

from app.schemas.note import NoteCreate, NoteRead, NoteUpdate, NoteResponse
from app.services.note_service import NoteService
from app.repositories.note_repository import NoteRepository
from app.core.cassandra import cassandra_config

router = APIRouter()

def get_note_service():
    try:
        if not cassandra_config.get_session():
            cassandra_config.connect()
        
        repository = NoteRepository()
        service = NoteService(repository)
        return service
    except Exception as e:
        logging.error(f"Error initializing NoteService: {e}")
        raise HTTPException(status_code=500, detail="Internal server error")

@router.post("/notes", response_model=NoteRead, status_code=201)
def create_note(note_data: NoteCreate):
    try:
        logging.info(f"Received request to create note: {note_data}")
        service = get_note_service()
        note = service.create_note(note_data)
        result = service.note_to_read_schema(note)
        logging.info(f"Created note successfully: {result}")
        return result
        
    except ValueError as e:
        logging.error(f"Validation error creating note: {e}")
        raise HTTPException(
            status_code=422,
            detail=str(e)
        )
    except Exception as e:
        logging.error(f"Error creating note: {e}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.get("/notes/{note_id}", response_model=NoteRead)
def get_note_by_id(note_id: str, country: str = "US"):
    try:
        try:
            note_id_int = int(note_id)
        except (ValueError, TypeError):
            raise HTTPException(
                status_code=400,
                detail="Invalid note ID format, must be integer"
            )
        
        service = get_note_service()
        note = service.get_note_by_id(note_id_int, country)
        
        if not note:
            raise HTTPException(
                status_code=404,
                detail="Note not found"
            )
        
        return service.note_to_read_schema(note)
        
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error getting note {note_id}: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.get("/notes", response_model=List[NoteRead])
def get_notes_by_issue_id(
    issue_id: Optional[int] = Query(None, description="ID Issue для фильтрации заметок")
):
    try:
        service = get_note_service()
        
        if issue_id is not None:
            notes = service.get_notes_by_issue_id(issue_id)
        else:
            notes = []
        
        return [service.note_to_read_schema(note) for note in notes]
        
    except Exception as e:
        logging.error(f"Error getting notes: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.put("/notes/{note_id}", response_model=NoteRead)
async def update_note_by_id(
    note_id: str,
    request: Request,
    issueId: Optional[int] = Query(None),
    content: Optional[str] = Query(None),
    country: str = Query("US")
):
    try:
        try:
            note_id_int = int(note_id)
        except (ValueError, TypeError):
            raise HTTPException(
                status_code=400,
                detail="Invalid note ID format, must be integer"
            )
        
        payload = {}
        raw_body = await request.body()
        if not raw_body:
            logging.warning(
                f"update_note_by_id: empty body; content-length={request.headers.get('content-length')}; "
                f"content-type={request.headers.get('content-type')}; expect={request.headers.get('expect')}"
            )
        else:
            try:
                raw_text = raw_body.decode("utf-8-sig")
                parsed = json.loads(raw_text)
            except Exception as e:
                snippet = raw_body[:200]
                logging.warning(
                    f"update_note_by_id: JSON parse failed: {e}; content-length={request.headers.get('content-length')}; "
                    f"content-type={request.headers.get('content-type')}; snippet={snippet!r}"
                )
                parsed = None

            if isinstance(parsed, dict):
                payload.update({str(k).strip(): v for k, v in parsed.items()})

        if "content" not in payload or payload.get("content") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str) and k.strip().lower() == "content":
                    payload["content"] = v
                    break

        if "content" not in payload or payload.get("content") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str):
                    key_norm = "".join(ch for ch in k if ch.isalnum()).lower()
                    if key_norm == "content":
                        payload["content"] = v
                        break

        if "issueId" not in payload or payload.get("issueId") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str) and k.strip().lower() == "issueid":
                    payload["issueId"] = v
                    break

        if "issueId" not in payload or payload.get("issueId") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str):
                    key_norm = "".join(ch for ch in k if ch.isalnum()).lower()
                    if key_norm == "issueid":
                        payload["issueId"] = v
                        break

        payload["id"] = note_id_int
        payload.setdefault("country", country)
        if issueId is not None:
            payload["issueId"] = issueId
        if content is not None:
            payload["content"] = content
 
        service = get_note_service()
        if payload.get("issueId") is None:
            existing_note = service.get_note_by_id(note_id_int, payload.get("country", "US"))
            if existing_note:
                payload["issueId"] = existing_note.issueid

        if payload.get("content") is None:
            logging.warning(
                f"update_note_by_id: content still missing before validation; keys={list(payload.keys())}; "
                f"content-type={request.headers.get('content-type')}"
            )

        try:
            note_update = NoteUpdate(**payload)
        except ValidationError as e:
            raise HTTPException(status_code=422, detail=e.errors())
 
        note = service.update_note(note_update)
         
        if not note:
            raise HTTPException(
                status_code=404,
                detail="Note not found"
            )
         
        return service.note_to_read_schema(note)
         
    except ValueError as e:
        raise HTTPException(
            status_code=422,
            detail=str(e)
        )
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating note {note_id}: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.put("/notes", response_model=NoteRead)
async def update_note(
    request: Request,
    id: Optional[int] = Query(None),
    issueId: Optional[int] = Query(None),
    content: Optional[str] = Query(None),
    country: str = Query("US")
):
    try:
        payload = {}
        raw_body = await request.body()
        if not raw_body:
            logging.warning(
                f"update_note: empty body; content-length={request.headers.get('content-length')}; "
                f"content-type={request.headers.get('content-type')}; expect={request.headers.get('expect')}"
            )
        else:
            try:
                raw_text = raw_body.decode("utf-8-sig")
                parsed = json.loads(raw_text)
            except Exception as e:
                snippet = raw_body[:200]
                logging.warning(
                    f"update_note: JSON parse failed: {e}; content-length={request.headers.get('content-length')}; "
                    f"content-type={request.headers.get('content-type')}; snippet={snippet!r}"
                )
                parsed = None

            if isinstance(parsed, dict):
                payload.update({str(k).strip(): v for k, v in parsed.items()})

        if "content" not in payload or payload.get("content") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str) and k.strip().lower() == "content":
                    payload["content"] = v
                    break

        if "content" not in payload or payload.get("content") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str):
                    key_norm = "".join(ch for ch in k if ch.isalnum()).lower()
                    if key_norm == "content":
                        payload["content"] = v
                        break

        if "issueId" not in payload or payload.get("issueId") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str) and k.strip().lower() == "issueid":
                    payload["issueId"] = v
                    break

        if "issueId" not in payload or payload.get("issueId") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str):
                    key_norm = "".join(ch for ch in k if ch.isalnum()).lower()
                    if key_norm == "issueid":
                        payload["issueId"] = v
                        break

        if "id" not in payload or payload.get("id") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str) and k.strip().lower() == "id":
                    payload["id"] = v
                    break

        if "id" not in payload or payload.get("id") is None:
            for k, v in list(payload.items()):
                if isinstance(k, str):
                    key_norm = "".join(ch for ch in k if ch.isalnum()).lower()
                    if key_norm == "id":
                        payload["id"] = v
                        break

        if id is not None:
            payload["id"] = id
        payload.setdefault("country", country)
        if issueId is not None:
            payload["issueId"] = issueId
        if content is not None:
            payload["content"] = content
 
        service = get_note_service()
        if payload.get("id") is not None and payload.get("issueId") is None:
            existing_note = service.get_note_by_id(int(payload["id"]), payload.get("country", "US"))
            if existing_note:
                payload["issueId"] = existing_note.issueid

        if payload.get("content") is None:
            logging.warning(
                f"update_note: content still missing before validation; keys={list(payload.keys())}; "
                f"content-type={request.headers.get('content-type')}"
            )

        try:
            note_update = NoteUpdate(**payload)
        except ValidationError as e:
            raise HTTPException(status_code=422, detail=e.errors())
 
        note = service.update_note(note_update)
         
        if not note:
            raise HTTPException(
                status_code=404,
                detail="Note not found"
            )
         
        return service.note_to_read_schema(note)
         
    except ValueError as e:
        raise HTTPException(
            status_code=422,
            detail=str(e)
        )
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating note: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.delete("/notes/{note_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_note(note_id: str, country: str = "US"):
    try:
        try:
            note_id_int = int(note_id)
        except (ValueError, TypeError):
            raise HTTPException(
                status_code=400,
                detail="Invalid note ID format, must be integer"
            )
        
        service = get_note_service()
        success = service.delete_note(note_id_int, country)
        
        if not success:
            raise HTTPException(
                status_code=404,
                detail="Note not found"
            )
        
        return None
        
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error deleting note {note_id}: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.get("/health")
def health_check():
    try:
        session = cassandra_config.get_session()
        if session:
            session.execute("SELECT now() FROM system.local;")
            return {"status": "healthy", "database": "cassandra"}
        else:
            return {"status": "unhealthy", "database": "cassandra"}
    except Exception as e:
        logging.error(f"Health check failed: {e}")
        return {"status": "unhealthy", "database": "cassandra", "error": str(e)}
