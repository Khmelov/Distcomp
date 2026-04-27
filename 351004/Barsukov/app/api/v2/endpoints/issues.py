from fastapi import APIRouter, status, Depends, HTTPException, Body
from sqlalchemy.orm import Session
from database import get_db
from schemas.issue import IssueRequestTo, IssueResponseTo
from services.issue_service import IssueService
from api.v2.deps import get_current_user
from typing import List
import models

router = APIRouter()
service = IssueService()


@router.post("", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: IssueRequestTo, db: Session = Depends(get_db),
                 current_user: models.Author = Depends(get_current_user)):
    # Только ADMIN может создавать посты от чужого имени
    if current_user.role != "ADMIN" and dto.authorId != current_user.id:
        dto.authorId = current_user.id

    res = service.create(db, dto)
    return IssueResponseTo(
        id=res.id, authorId=res.author_id, title=res.title,
        content=res.content, created=str(res.created), modified=str(res.modified)
    )


@router.get("", response_model=List[IssueResponseTo])
async def get_all(skip: int = 0, limit: int = 10, db: Session = Depends(get_db),
                  current_user=Depends(get_current_user)):
    # Чтение доступно всем авторизованным пользователям
    items = service.get_all(db, skip, limit)
    return [
        IssueResponseTo(
            id=i.id, authorId=i.author_id, title=i.title,
            content=i.content, created=str(i.created), modified=str(i.modified)
        ) for i in items
    ]


@router.get("/{id}", response_model=IssueResponseTo)
async def get_by_id(id: int, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    res = await service.get_by_id(db, id)
    if not res:
        raise HTTPException(status_code=404, detail="Issue not found")
    return res


@router.put("/{id}", response_model=IssueResponseTo)
async def update(id: int, dto: IssueRequestTo, db: Session = Depends(get_db),
                 current_user: models.Author = Depends(get_current_user)):
    existing_issue = await service.get_by_id(db, id)
    if not existing_issue:
        raise HTTPException(status_code=404, detail="Issue not found")

    # Проверка прав
    if current_user.role != "ADMIN" and existing_issue.authorId != current_user.id:
        raise HTTPException(status_code=403, detail="Forbidden: You can only update your own issues")

    res = await service.update(db, id, dto)

    return IssueResponseTo(
        id=res.id,
        authorId=res.author_id,
        title=res.title,
        content=res.content,
        created=str(res.created),
        modified=str(res.modified)
    )


@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int, db: Session = Depends(get_db), current_user: models.Author = Depends(get_current_user)):
    existing_issue = await service.get_by_id(db, id)
    if not existing_issue:
        raise HTTPException(status_code=404, detail="Issue not found")

    if current_user.role != "ADMIN" and existing_issue.authorId != current_user.id:
        raise HTTPException(status_code=403, detail="Forbidden: You can only delete your own issues")

    await service.delete(db, id)