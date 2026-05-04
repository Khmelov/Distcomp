from typing import List

from fastapi import APIRouter

from discussion_mod.api.v1.dep import NoteServiceDep
from discussion_mod.schemas.note import NoteRequestTo, NoteResponseTo

router = APIRouter(prefix="/notes")


@router.get("/by-news/{news_id}", response_model=List[NoteResponseTo])
def list_by_news(news_id: int, service: NoteServiceDep):
    return service.by_news_id(news_id)


@router.delete("/by-news/{news_id}", status_code=204)
def delete_by_news(news_id: int, service: NoteServiceDep):
    service.delete_all_for_news(news_id)


@router.post("", response_model=NoteResponseTo, status_code=201)
def create_note(dto: NoteRequestTo, service: NoteServiceDep):
    return service.create(dto)


@router.get("", response_model=List[NoteResponseTo])
def get_notes(service: NoteServiceDep):
    return service.get_all()


@router.get("/{note_id}", response_model=NoteResponseTo)
def get_note(note_id: int, service: NoteServiceDep):
    return service.get_one(note_id)


@router.put("/{note_id}", response_model=NoteResponseTo)
def update_note(note_id: int, dto: NoteRequestTo, service: NoteServiceDep):
    return service.update(note_id, dto)


@router.delete("/{note_id}", status_code=204)
def delete_note(note_id: int, service: NoteServiceDep):
    service.delete(note_id)
