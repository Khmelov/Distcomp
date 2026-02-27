from fastapi import APIRouter, status, Body, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from app.schemas.issue import IssueRequestTo, IssueResponseTo
from app.services.issue_service import IssueService
from typing import List

router = APIRouter()
service = IssueService()

@router.post("", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: IssueRequestTo = Body(...), db: Session = Depends(get_db)):
    res = service.create(db, dto)
    # Мапим author_id -> authorId для схемы
    return IssueResponseTo(
        id=res.id, authorId=res.author_id, title=res.title,
        content=res.content, created=str(res.created), modified=str(res.modified)
    )

@router.get("", response_model=List[IssueResponseTo])
async def get_all(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    items = service.get_all(db, skip, limit)
    return [
        IssueResponseTo(
            id=i.id, authorId=i.author_id, title=i.title,
            content=i.content, created=str(i.created), modified=str(i.modified)
        ) for i in items
    ]

@router.get("/{id}", response_model=IssueResponseTo)
async def get_by_id(id: int, db: Session = Depends(get_db)):
    i = service.get_by_id(db, id)
    if not i: raise HTTPException(404, "Issue not found")
    return IssueResponseTo(
        id=i.id, authorId=i.author_id, title=i.title,
        content=i.content, created=str(i.created), modified=str(i.modified)
    )

@router.put("/{id}", response_model=IssueResponseTo)
async def update(id: int, dto: IssueRequestTo = Body(...), db: Session = Depends(get_db)):
    res = service.update(db, id, dto)
    if not res: raise HTTPException(404, "Issue not found")
    return IssueResponseTo(
        id=res.id, authorId=res.author_id, title=res.title,
        content=res.content, created=str(res.created), modified=str(res.modified)
    )

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, db: Session = Depends(get_db)):
    if not service.delete(db, id):
        raise HTTPException(404, "Issue not found")