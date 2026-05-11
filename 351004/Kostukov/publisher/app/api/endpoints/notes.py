from fastapi import APIRouter, status, HTTPException, Body, Depends
from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from publisher.app.core.notes.dto import NoteRequestTo, NoteResponseTo
from publisher.app.infrastructure.db.session import get_session
from publisher.app.services.note_service_kafka import service

router = APIRouter(prefix="/api/v1.0/notes", tags=["notes"])


def _to_response(item: dict) -> NoteResponseTo:
    return NoteResponseTo(
        id=item["id"],
        articleId=item["articleId"],
        content=item["content"],
        createdAt=item["createdAt"],
    )


@router.post("", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
async def create_note(dto: NoteRequestTo, session: AsyncSession = Depends(get_session)):
    item = await service.create(session, dto)
    return _to_response(item)


@router.get("", response_model=List[NoteResponseTo])
@router.get("/", response_model=List[NoteResponseTo])
async def list_notes():
    items = await service.get_all()
    return [_to_response(i) for i in items]


@router.get("/{note_id}", response_model=NoteResponseTo)
async def get_note(note_id: int):
    item = await service.get_by_id(note_id)
    if not item:
        raise HTTPException(status_code=404, detail="Note not found")
    return _to_response(item)


@router.put("/{note_id}", response_model=NoteResponseTo)
@router.put("/{note_id}/", response_model=NoteResponseTo)
async def update_note(
    note_id: int,
    payload: NoteRequestTo = Body(...),
    session: AsyncSession = Depends(get_session),
):
    item = await service.update(session, note_id, payload)
    if not item:
        raise HTTPException(status_code=404, detail="Note not found")
    return _to_response(item)


@router.delete("/{note_id}", status_code=status.HTTP_204_NO_CONTENT)
@router.delete("/{note_id}/", status_code=status.HTTP_204_NO_CONTENT)
async def delete_note(note_id: int):
    if not await service.delete(note_id):
        raise HTTPException(status_code=404, detail="Note not found")


@router.get("/by-article/{article_id}", response_model=List[NoteResponseTo])
@router.get("/by-article/{article_id}/", response_model=List[NoteResponseTo])
async def get_notes_by_article(article_id: int):
    items = await service.list_by_article_id(article_id)
    return [_to_response(i) for i in items]