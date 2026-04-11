from typing import List

from fastapi import APIRouter

from src.api.v1.dep import SessionDep
from src.schemas.dto import WriterRequestTo, WriterResponseTo
from src.services import WriterService

router = APIRouter(prefix="/writers")


@router.post("", response_model=WriterResponseTo, status_code=201)
def create_writer(dto: WriterRequestTo, db: SessionDep):
    return WriterService.create(db, dto)


@router.get("", response_model=List[WriterResponseTo])
def get_writers(db: SessionDep):
    return WriterService.get_all(db)


@router.get("/{id}", response_model=WriterResponseTo)
def get_writer(id: int, db: SessionDep):
    return WriterService.get_by_id(db, id)


@router.put("/{id}", response_model=WriterResponseTo)
def update_writer(id: int, dto: WriterRequestTo, db: SessionDep):
    return WriterService.update(db, id, dto)


@router.delete("/{id}", status_code=204)
def delete_writer(id: int, db: SessionDep):
    WriterService.delete(db, id)
