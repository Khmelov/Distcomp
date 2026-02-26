from fastapi import APIRouter, status, Body
from app.schemas.note import NoteRequestTo, NoteResponseTo
from app.services.note_service import NoteService
from typing import List

router = APIRouter()
service = NoteService()

@router.post("", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: NoteRequestTo = Body(...)):
    return service.create(dto)

@router.get("", response_model=List[NoteResponseTo])
async def get_all():
    return service.get_all()

@router.get("/{id}", response_model=NoteResponseTo)
async def get_by_id(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=NoteResponseTo)
async def update(id: int, dto: NoteRequestTo = Body(...)):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int):
    service.delete(id)
