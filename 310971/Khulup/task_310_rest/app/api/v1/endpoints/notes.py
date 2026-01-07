from fastapi import APIRouter, HTTPException
from app.schemas.note import NoteCreate, NoteRead
from app.models.note import Note
from app.repositories.note_repository import NoteRepository
from app.services.note_service import NoteService

router = APIRouter()
repo = NoteRepository()
service = NoteService(repo)

@router.post("", response_model=NoteRead, status_code=201)
def create_note(note: NoteCreate):
    return service.create_note(note)

@router.get("/{note_id}", response_model=NoteRead)
def get_note(note_id: int):
    note = service.get_note(note_id)
    if not note:
        raise HTTPException(status_code=404, detail="Note not found")
    return note

@router.get("", response_model=list[NoteRead])
def list_notes():
    return service.list_notes()

@router.delete("/{note_id}", status_code=204)
def delete_note(note_id: int):
    success = service.delete_note(note_id)
    if not success:
        raise HTTPException(status_code=404, detail="Note not found")

@router.put("", response_model=NoteRead)
def update_note(note: Note):
    updated = service.update_note(note)
    if not updated:
        raise HTTPException(status_code=404, detail="Note not found")
    return updated
