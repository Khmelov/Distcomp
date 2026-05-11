from fastapi import APIRouter, status, Body, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from schemas.issue import IssueRequestTo, IssueResponseTo
from services.issue_service import IssueService
from typing import List

router = APIRouter()
service = IssueService()

@router.post("", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: IssueRequestTo = Body(...), db: Session = Depends(get_db)):
    res = service.create(db, dto)
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
    # Сервис уже вернет готовый IssueResponseTo (из кеша или БД)
    res = await service.get_by_id(db, id)
    if not res:
        raise HTTPException(404, "Issue not found")
    return res

@router.put("/{id}", response_model=IssueResponseTo)
async def update(id: int, dto: IssueRequestTo = Body(...), db: Session = Depends(get_db)):
    res = await service.update(db, id, dto)
    if not res:
        raise HTTPException(404, "Issue not found")
    # Мапим в схему, если сервис вернул объект БД
    return IssueResponseTo(
        id=res.id, authorId=res.author_id, title=res.title,
        content=res.content, created=str(res.created), modified=str(res.modified)
    )

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, db: Session = Depends(get_db)):
    if not await service.delete(db, id):
        raise HTTPException(404, "Issue not found")