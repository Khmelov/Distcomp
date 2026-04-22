from fastapi import APIRouter, status, Depends, HTTPException, Body
from typing import List

from sqlalchemy.ext.asyncio import AsyncSession
from app.infrastructure.db.session import get_session
from app.core.notes.dto import NoteRequestTo, NoteResponseTo
from app.core.notes.repo import InMemoryNoteRepo
from app.core.notes.service import NoteService as InMemoryNoteService
from app.services.note_service import NoteService

router = APIRouter(prefix="/api/v1.0/notes", tags=["notes"])
_note_repo = InMemoryNoteRepo()

try:
    from app.core.articles.repo import InMemoryArticleRepo as InMemoryArticleRepoImpl
except Exception:
    InMemoryArticleRepoImpl = None

_article_repo = InMemoryArticleRepoImpl() if InMemoryArticleRepoImpl else None

_note_service = InMemoryNoteService(_note_repo, _article_repo)

service = NoteService()


@router.post("", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
async def create_note(dto: NoteRequestTo, session: AsyncSession = Depends(get_session)):
    item = await service.create(session, dto)
    return NoteResponseTo(
        id = item.id, articleId=item.article_id,
        content= item.content, createdAt=item.created_at
    )

@router.get("", response_model=List[NoteResponseTo])
@router.get("/", response_model=List[NoteResponseTo])
async def list_notes(session: AsyncSession = Depends(get_session)):
    items = await service.get_all(session)
    return [
        NoteResponseTo(
            id = i.id, articleId=i.article_id,
            content=i.content, createdAt=i.created_at
        ) for i in items
    ]

@router.get("/{note_id}", response_model=NoteResponseTo)
async def get_note(note_id: int, session: AsyncSession = Depends(get_session)):
    item = await service.get_by_id(session, note_id)
    if not item: raise HTTPException(status_code=404, detail="Note not found")
    return NoteResponseTo(
        id = item.id, articleId=item.article_id,
        content=item.content, createdAt=item.created_at
    )

@router.put("/{note_id}", response_model=NoteResponseTo)
@router.put("/{note_id}/", response_model=NoteResponseTo)
async def update_note(note_id: int, payload: NoteRequestTo = Body(...), session: AsyncSession = Depends(get_session)):
    item = await service.update(session, note_id, payload)
    if not item: raise HTTPException(status_code=404, detail="Note not found")
    return NoteResponseTo(
        id=item.id, articleId=item.article_id,
        content=item.content, createdAt=item.created_at
    )

@router.delete("/{note_id}", status_code=status.HTTP_204_NO_CONTENT)
@router.delete("/{note_id}/", status_code=status.HTTP_204_NO_CONTENT)
async def delete_note(note_id: int, session: AsyncSession = Depends(get_session)):
    if not await service.delete(session, note_id):
        raise HTTPException(status_code=404, detail="Note not found")

@router.get("/by-article/{article_id}", response_model=List[NoteResponseTo])
@router.get("/by-article/{article_id}/", response_model=List[NoteResponseTo])
async def get_notes_by_article(article_id: int):
    return _note_service.list_by_article_id(article_id)
