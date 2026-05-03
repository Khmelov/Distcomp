from fastapi import APIRouter, Depends
from fastapi.responses import JSONResponse
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from typing import List
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.issue import IssueCreate, IssueUpdate, IssueResponse
from app.services.issue_service import IssueService
from app.auth import get_current_user, security

router = APIRouter(prefix="/issues", tags=["issues-v2"])


@router.get("")
def get_issues(credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    results = IssueService(db).get_all()
    return JSONResponse(content=[r.model_dump(by_alias=True) for r in results])


@router.get("/{issue_id}")
def get_issue(issue_id: int, credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    return JSONResponse(content=IssueService(db).get_by_id(issue_id).model_dump(by_alias=True))


@router.post("", status_code=201)
def create_issue(data: IssueCreate, credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    result = IssueService(db).create(data)
    return JSONResponse(status_code=201, content=result.model_dump(by_alias=True))


@router.put("/{issue_id}")
def update_issue(issue_id: int, data: IssueUpdate, credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    data.id = issue_id
    return JSONResponse(content=IssueService(db).update(data).model_dump(by_alias=True))


@router.delete("/{issue_id}", status_code=204)
def delete_issue(issue_id: int, credentials: HTTPAuthorizationCredentials = Depends(security), db: Session = Depends(get_db)):
    get_current_user(credentials)
    IssueService(db).delete(issue_id)
