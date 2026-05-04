from typing import List

from fastapi import APIRouter, Body, Depends
from sqlalchemy.orm import Session

from src.core.database import get_db
from src.schemas.dto import LabelRequestTo, LabelResponseTo
from src.services import LabelService

router = APIRouter(prefix="/labels")


@router.post("", response_model=LabelResponseTo, status_code=201)
def create_label(dto: LabelRequestTo = Body(...), db: Session = Depends(get_db)):
    return LabelService.create(db, dto)


@router.get("", response_model=List[LabelResponseTo])
def get_labels(db: Session = Depends(get_db)):
    return LabelService.get_all(db)


@router.get("/{id}", response_model=LabelResponseTo)
def get_label(id: int, db: Session = Depends(get_db)):
    return LabelService.get_by_id(db, id)


@router.put("/{id}", response_model=LabelResponseTo)
def update_label(id: int, dto: LabelRequestTo = Body(...), db: Session = Depends(get_db)):
    return LabelService.update(db, id, dto)


@router.delete("/{id}", status_code=204)
def delete_label(id: int, db: Session = Depends(get_db)):
    LabelService.delete(db, id)
