from fastapi import APIRouter, status
from typing import List

from app.core.notes.dto import NoteRequestTo, NoteResponseTo
from app.core.notes.repo import InMemoryNoteRepo
from app.core.notes.service import NoteService

router = APIRouter(prefix="/api/v1.0/notes", tags=["notes"])
_note_repo = InMemoryNoteRepo()

try:
    from app.core.articles.repo import InMemoryArticleRepo as InMemoryArticleRepoImpl
except Exception:
    InMemoryArticleRepoImpl = None

_article_repo = InMemoryArticleRepoImpl() if InMemoryArticleRepoImpl else None

_note_service = NoteService(_note_repo, _article_repo)


@router.post("", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
async def create_note(dto: NoteRequestTo):
    created = _note_service.create_note(dto)
    return created


@router.get("", response_model=List[NoteResponseTo])
@router.get("/", response_model=List[NoteResponseTo])
async def list_notes():
    return _note_service.list_notes()


@router.get("/{note_id}", response_model=NoteResponseTo)
async def get_note(note_id: int):
    return _note_service.get_by_id(note_id)


@router.put("/{note_id}", response_model=NoteResponseTo)
@router.put("/{note_id}/", response_model=NoteResponseTo)
async def update_note(note_id: int, dto: NoteRequestTo):
    updated = _note_service.update_note(note_id, dto)
    return updated

@router.delete("/{note_id}", status_code=status.HTTP_204_NO_CONTENT)
@router.delete("/{note_id}/", status_code=status.HTTP_204_NO_CONTENT)
async def delete_note(note_id: int):
    _note_service.delete_note(note_id)
    return None

@router.get("/by-article/{article_id}", response_model=List[NoteResponseTo])
@router.get("/by-article/{article_id}/", response_model=List[NoteResponseTo])
async def get_notes_by_article(article_id: int):
    return _note_service.list_by_article_id(article_id)
