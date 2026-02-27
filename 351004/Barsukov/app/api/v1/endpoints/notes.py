from fastapi import APIRouter, status, Body, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from app.schemas.note import NoteRequestTo, NoteResponseTo
from app.services.note_service import NoteService
from typing import List

router = APIRouter()
service = NoteService()

@router.post("", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: NoteRequestTo = Body(...), db: Session = Depends(get_db)):
    res = service.create(db, dto)
    return NoteResponseTo(id=res.id, issueId=res.issue_id, content=res.content)

@router.get("", response_model=List[NoteResponseTo])
async def get_all(db: Session = Depends(get_db)):
    items = service.get_all(db)
    return [NoteResponseTo(id=i.id, issueId=i.issue_id, content=i.content) for i in items]

@router.get("/{id}", response_model=NoteResponseTo)
async def get_by_id(id: int, db: Session = Depends(get_db)):
    res = service.get_by_id(db, id)
    if not res: raise HTTPException(404, "Note not found")
    return NoteResponseTo(id=res.id, issueId=res.issue_id, content=res.content)

@router.put("/{id}", response_model=NoteResponseTo)
async def update(id: int, dto: NoteRequestTo = Body(...), db: Session = Depends(get_db)):
    res = service.update(db, id, dto)
    if not res: raise HTTPException(404, "Note not found")
    return NoteResponseTo(id=res.id, issueId=res.issue_id, content=res.content)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, db: Session = Depends(get_db)):
    if not service.delete(db, id):
        raise HTTPException(404, "Note not found")