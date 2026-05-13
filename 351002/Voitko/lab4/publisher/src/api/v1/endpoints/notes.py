from typing import List

from fastapi import APIRouter, Body, Depends
from sqlalchemy.orm import Session

from src.core.database import get_db
from src.schemas.dto import NoteRequestTo, NoteResponseTo
from src.services import NoteService

router = APIRouter(prefix="/notes")


@router.post("", response_model=NoteResponseTo, status_code=201)
def create_note(dto: NoteRequestTo = Body(...), db: Session = Depends(get_db)):
    return NoteService.create(db, dto)


@router.get("", response_model=List[NoteResponseTo])
def get_notes(db: Session = Depends(get_db)):
    return NoteService.get_all(db)


@router.get("/{id}", response_model=NoteResponseTo)
def get_note(id: int, db: Session = Depends(get_db)):
    return NoteService.get_by_id(db, id)


@router.put("/{id}", response_model=NoteResponseTo)
def update_note(id: int, dto: NoteRequestTo = Body(...), db: Session = Depends(get_db)):
    return NoteService.update(db, id, dto)


@router.delete("/{id}", status_code=204)
def delete_note(id: int, db: Session = Depends(get_db)):
    NoteService.delete(db, id)
