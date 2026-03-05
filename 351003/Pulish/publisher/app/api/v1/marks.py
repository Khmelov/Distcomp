from fastapi import APIRouter, status, Depends
from sqlalchemy.orm import Session
from app.db.database import get_db
from app.dto.mark import MarkRequestTo, MarkResponseTo
from app.services.mark_service import MarkService

router = APIRouter()


@router.post("/marks", response_model=MarkResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_mark(dto: MarkRequestTo, db: Session = Depends(get_db)):
    return MarkService(db).create(dto)


@router.get("/marks", response_model=list[MarkResponseTo])
def get_marks(db: Session = Depends(get_db)):
    return MarkService(db).find_all()


@router.get("/marks/{id}", response_model=MarkResponseTo)
def get_mark(id: int, db: Session = Depends(get_db)):
    return MarkService(db).find_by_id(id)


@router.put("/marks", response_model=MarkResponseTo)
def update_mark(dto: MarkRequestTo, db: Session = Depends(get_db)):
    return MarkService(db).update(dto)


@router.delete("/marks/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_mark(id: int, db: Session = Depends(get_db)):
    MarkService(db).delete(id)
