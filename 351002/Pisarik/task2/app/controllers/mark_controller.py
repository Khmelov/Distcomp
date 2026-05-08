from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session

from app.dtos.mark_request import MarkRequestTo
from app.dtos.mark_response import MarkResponseTo
from app.db.database import get_db
from app.repositories.sqlalchemy_repository import PageParams
from app.services.mark_service import MarkService

router = APIRouter(prefix="/api/v1.0/marks", tags=["marks"])


def get_mark_service(db: Session = Depends(get_db)) -> MarkService:
    return MarkService(db)


@router.post("", response_model=MarkResponseTo, status_code=status.HTTP_201_CREATED)
def create_mark(
    dto: MarkRequestTo,
    service: MarkService = Depends(get_mark_service),
) -> MarkResponseTo:
    return service.create_mark(dto)


@router.get("", response_model=List[MarkResponseTo])
def list_marks(
    page: int = Query(0, ge=0),
    size: int = Query(20, ge=1, le=200),
    sort: str = Query("id,asc"),
    name: Optional[str] = None,
    service: MarkService = Depends(get_mark_service),
) -> List[MarkResponseTo]:
    return service.get_all_marks(PageParams(page=page, size=size, sort=sort), name=name)


@router.get("/{mark_id}", response_model=MarkResponseTo)
def get_mark(
    mark_id: int,
    service: MarkService = Depends(get_mark_service),
) -> MarkResponseTo:
    mark = service.get_mark(mark_id)
    if not mark:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Mark not found", "errorCode": 40401},
        )
    return mark


@router.put("/{mark_id}", response_model=MarkResponseTo)
def update_mark(
    mark_id: int,
    dto: MarkRequestTo,
    service: MarkService = Depends(get_mark_service),
) -> MarkResponseTo:
    updated = service.update_mark(mark_id, dto)
    if not updated:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Mark not found", "errorCode": 40401},
        )
    return updated


@router.delete("/{mark_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_mark(
    mark_id: int,
    service: MarkService = Depends(get_mark_service),
) -> None:
    deleted = service.delete_mark(mark_id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Mark not found", "errorCode": 40401},
        )