from fastapi import APIRouter, Depends, status
from fastapi.responses import JSONResponse
from typing import List
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.issue import IssueCreate, IssueUpdate, IssueResponse
from app.schemas.marker import MarkerResponse
from app.services.issue_service import IssueService
from app import cache

router = APIRouter(prefix="/issues", tags=["issues"])


@router.get("", response_model=List[IssueResponse], status_code=status.HTTP_200_OK)
def get_issues(
    page: int = 0,
    size: int = 10000,
    sort_by: str = "id",
    sort_order: str = "asc",
    db: Session = Depends(get_db)
):
    cache_key = f"issues:all:{page}:{size}:{sort_by}:{sort_order}"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return JSONResponse(content=cached)
    results = IssueService(db).get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)
    data = [r.model_dump(by_alias=True) for r in results]
    cache.cache_set(cache_key, data)
    return JSONResponse(content=data)


@router.get("/{issue_id}", response_model=IssueResponse, status_code=status.HTTP_200_OK)
def get_issue(issue_id: int, db: Session = Depends(get_db)):
    cache_key = f"issues:{issue_id}"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return JSONResponse(content=cached)
    result = IssueService(db).get_by_id(issue_id)
    data = result.model_dump(by_alias=True)
    cache.cache_set(cache_key, data)
    return JSONResponse(content=data)


@router.post("", response_model=IssueResponse, status_code=status.HTTP_201_CREATED)
def create_issue(data: IssueCreate, db: Session = Depends(get_db)):
    result = IssueService(db).create(data)
    data_out = result.model_dump(by_alias=True)
    cache.cache_set(f"issues:{result.id}", data_out)
    cache.cache_delete_pattern("issues:all:*")
    return JSONResponse(status_code=201, content=data_out)


@router.put("/{issue_id}", response_model=IssueResponse, status_code=status.HTTP_200_OK)
def update_issue(issue_id: int, data: IssueUpdate, db: Session = Depends(get_db)):
    data.id = issue_id
    result = IssueService(db).update(data)
    data_out = result.model_dump(by_alias=True)
    cache.cache_set(f"issues:{issue_id}", data_out)
    cache.cache_delete_pattern("issues:all:*")
    return JSONResponse(content=data_out)


@router.delete("/{issue_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_issue(issue_id: int, db: Session = Depends(get_db)):
    IssueService(db).delete(issue_id)
    cache.cache_delete(f"issues:{issue_id}")
    cache.cache_delete_pattern("issues:all:*")


@router.get("/{issue_id}/markers", response_model=List[MarkerResponse], status_code=status.HTTP_200_OK)
def get_issue_markers(issue_id: int, db: Session = Depends(get_db)):
    return IssueService(db).get_markers(issue_id)


@router.post("/{issue_id}/markers/{marker_id}", response_model=MarkerResponse, status_code=status.HTTP_201_CREATED)
def add_marker_to_issue(issue_id: int, marker_id: int, db: Session = Depends(get_db)):
    return IssueService(db).add_marker(issue_id, marker_id)


@router.delete("/{issue_id}/markers/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
def remove_marker_from_issue(issue_id: int, marker_id: int, db: Session = Depends(get_db)):
    IssueService(db).remove_marker(issue_id, marker_id)
