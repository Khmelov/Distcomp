from typing import List

from fastapi import APIRouter, Body, Depends
from sqlalchemy.orm import Session

from src.core.database import get_db
from src.schemas.dto import WriterRequestTo, WriterResponseTo
from src.services import WriterService

router = APIRouter(prefix="/writers")


@router.post("", response_model=WriterResponseTo, status_code=201)
def create_writer(dto: WriterRequestTo = Body(...), db: Session = Depends(get_db)):
    return WriterService.create(db, dto)


@router.get("", response_model=List[WriterResponseTo])
def get_writers(db: Session = Depends(get_db)):
    return WriterService.get_all(db)


@router.get("/{id}", response_model=WriterResponseTo)
def get_writer(id: int, db: Session = Depends(get_db)):
    return WriterService.get_by_id(db, id)


@router.put("/{id}", response_model=WriterResponseTo)
def update_writer(id: int, dto: WriterRequestTo = Body(...), db: Session = Depends(get_db)):
    return WriterService.update(db, id, dto)


@router.delete("/{id}", status_code=204)
def delete_writer(id: int, db: Session = Depends(get_db)):
    WriterService.delete(db, id)
