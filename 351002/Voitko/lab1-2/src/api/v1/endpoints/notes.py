from typing import List

from fastapi import APIRouter

from src.api.v1.dep import SessionDep
from src.schemas.dto import NoteRequestTo, NoteResponseTo
from src.services import NoteService

router = APIRouter(prefix="/notes")


@router.post("", response_model=NoteResponseTo, status_code=201)
def create_note(dto: NoteRequestTo, db: SessionDep):
    return NoteService.create(db, dto)


@router.get("", response_model=List[NoteResponseTo])
def get_notes(db: SessionDep):
    return NoteService.get_all(db)


@router.get("/{id}", response_model=NoteResponseTo)
def get_note(id: int, db: SessionDep):
    return NoteService.get_by_id(db, id)


@router.put("/{id}", response_model=NoteResponseTo)
def update_note(id: int, dto: NoteRequestTo, db: SessionDep):
    return NoteService.update(db, id, dto)


@router.delete("/{id}", status_code=204)
def delete_note(id: int, db: SessionDep):
    NoteService.delete(db, id)
