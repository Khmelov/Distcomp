from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.api.v1.dep import get_note_service
from src.schemas.note import NoteResponseTo, NoteRequestTo
from src.services import NoteService

router = APIRouter(prefix="/notes")

@router.get("", response_model=List[NoteResponseTo], status_code=HTTPStatus.OK)
def get_all(service: NoteService = Depends(get_note_service)):
    return service.get_all()

@router.get("/{note_id}", response_model=NoteResponseTo, status_code=HTTPStatus.OK)
def get_by_id(note_id: int, service: NoteService = Depends(get_note_service)):
    return service.get_one(note_id)

@router.post("", response_model=NoteResponseTo, status_code=HTTPStatus.CREATED)
def create(dto: NoteRequestTo, service: NoteService = Depends(get_note_service)):
    return service.create(dto)

@router.put("/{note_id}", response_model=NoteResponseTo, status_code=HTTPStatus.OK)
def put(note_id: int, dto: NoteRequestTo, service: NoteService = Depends(get_note_service)):
    return service.update(note_id, dto)

@router.delete("/{note_id}", status_code=HTTPStatus.NO_CONTENT)
def delete(note_id: int, service: NoteService = Depends(get_note_service)):
    return service.delete(note_id)