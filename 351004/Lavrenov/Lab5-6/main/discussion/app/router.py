from typing import List

from fastapi import APIRouter, Depends, Request, status

from discussion.app.schemas import NoteRequestTo, NoteResponseTo
from discussion.app.services.note_service import NoteService

router = APIRouter(prefix="/api/v1.0/notes", tags=["notes"])


def get_note_service(request: Request) -> NoteService:
    service = getattr(request.app.state, "note_service", None)
    if service is None:
        raise RuntimeError("Note service is not initialized")
    return service


@router.post("", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
async def create_note(
    payload: NoteRequestTo,
    service: NoteService = Depends(get_note_service),
):
    return service.create(payload)


@router.get("", response_model=List[NoteResponseTo])
@router.get("/", response_model=List[NoteResponseTo])
async def list_notes(
    skip: int = 0,
    limit: int = 10,
    service: NoteService = Depends(get_note_service),
):
    return service.get_all(skip=skip, limit=limit)


@router.get("/{note_id}", response_model=NoteResponseTo)
async def get_note(
    note_id: int,
    service: NoteService = Depends(get_note_service),
):
    return service.get_by_id(note_id)


@router.put("/{note_id}", response_model=NoteResponseTo)
@router.put("/{note_id}/", response_model=NoteResponseTo)
async def update_note(
    note_id: int,
    payload: NoteRequestTo,
    service: NoteService = Depends(get_note_service),
):
    return service.update(note_id, payload)


@router.delete("/{note_id}", status_code=status.HTTP_204_NO_CONTENT)
@router.delete("/{note_id}/", status_code=status.HTTP_204_NO_CONTENT)
async def delete_note(
    note_id: int,
    service: NoteService = Depends(get_note_service),
):
    service.delete(note_id)


@router.get("/by-article/{article_id}", response_model=List[NoteResponseTo])
@router.get("/by-article/{article_id}/", response_model=List[NoteResponseTo])
async def get_notes_by_article(
    article_id: int,
    service: NoteService = Depends(get_note_service),
):
    return service.get_by_article_id(article_id)