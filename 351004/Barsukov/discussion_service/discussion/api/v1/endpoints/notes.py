from fastapi import APIRouter, status, Depends, HTTPException
from typing import List
from discussion.schemas.note import NoteRequestTo, NoteResponseTo
from discussion.services.note_service import NoteService
from discussion.db.thecassandra import get_db
from pydantic import BaseModel
router = APIRouter()

def get_service(session=Depends(get_db)):
    return NoteService(session)

@router.post("/notes", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
async def create_note(
    dto: NoteRequestTo,
    service: NoteService = Depends(get_service)
):
    """Создать новую заметку"""
    return service.create(dto)

@router.get("/notes", response_model=List[NoteResponseTo])
async def get_all_notes(
    service: NoteService = Depends(get_service)
):
    """Получить все заметки (может быть медленно)"""
    return service.get_all()

@router.get("/issues/{issue_id}/notes", response_model=List[NoteResponseTo])
async def get_notes_by_issue(
    issue_id: int,
    service: NoteService = Depends(get_service)
):
    """Получить все заметки для конкретного issue (эффективно)"""
    return service.get_by_issue(issue_id)

@router.get("/issues/{issue_id}/notes/{note_id}", response_model=NoteResponseTo)
async def get_note_by_issue_and_id(
    issue_id: int,
    note_id: int,
    service: NoteService = Depends(get_service)
):
    """Получить конкретную заметку по issue_id и note_id (эффективно)"""
    return service.get_by_issue_and_id(issue_id, note_id)

@router.put("/issues/{issue_id}/notes/{note_id}", response_model=NoteResponseTo)
async def update_note(
    issue_id: int,
    note_id: int,
    dto: NoteRequestTo,
    service: NoteService = Depends(get_service)
):
    """Обновить заметку"""
    return service.update(issue_id, note_id, dto)

@router.delete("/issues/{issue_id}/notes/{note_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_note(
    issue_id: int,
    note_id: int,
    service: NoteService = Depends(get_service)
):
    """Удалить заметку"""
    if not service.delete(issue_id, note_id):
        raise HTTPException(status_code=404, detail="Note not found")

# Для обратной совместимости со старым API
@router.get("/notes/{id}", response_model=NoteResponseTo)
async def get_note_by_id(
    id: int,
    service: NoteService = Depends(get_service)
):
    """
    Получить заметку по ID (неэффективно, лучше использовать /issues/{issue_id}/notes/{note_id})
    """
    notes = service.get_all()
    for note in notes:
        if note.id == id:
            return note
    raise HTTPException(status_code=404, detail="Note not found")

class StateUpdateRequest(BaseModel):
    state: str

@router.patch("/issues/{issue_id}/notes/{note_id}/state", response_model=NoteResponseTo)
async def update_note_state(
    issue_id: int,
    note_id: int,
    state_update: StateUpdateRequest,
    service: NoteService = Depends(get_service)
):
    """Обновить статус заметки"""
    return service.update_state(issue_id, note_id, state_update.state)