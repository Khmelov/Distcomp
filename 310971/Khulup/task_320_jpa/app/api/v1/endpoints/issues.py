from fastapi import APIRouter, HTTPException, Query
from app.schemas.issue import IssueCreate, IssueRead, IssueUpdate
from app.services.issue_service import IssueService
from app.repositories.issue_repository import IssueRepository
from app.core.db import SessionLocal
import logging
from psycopg2.errors import UniqueViolation

router = APIRouter()

@router.post("", response_model=IssueRead, status_code=201)
def create_issue(data: IssueCreate):
    session = SessionLocal()
    try:
        repo = IssueRepository(session)
        service = IssueService(repo)
        issue = service.create_issue(data)
        session.expunge_all()
        return issue
    except ValueError as e:
        if "does not exist" in str(e):
            raise HTTPException(status_code=400, detail="User with this id does not exist")
        elif "already exists" in str(e):
            raise HTTPException(status_code=403, detail="Issue with this title already exists")
        raise HTTPException(status_code=400, detail=str(e))
    except UniqueViolation as e:
        raise HTTPException(status_code=403, detail="Issue with this title already exists")
    except Exception as e:
        logging.error(f"Error creating issue: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.get("/{issue_id}", response_model=IssueRead)
def get_issue(issue_id: int):
    session = SessionLocal()
    try:
        repo = IssueRepository(session)
        service = IssueService(repo)
        issue = service.get_issue(issue_id)
        if not issue:
            raise HTTPException(status_code=404, detail="Issue not found")
        return issue
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error getting issue {issue_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.get("", response_model=list[IssueRead])
def list_issues(
    userId: int | None = Query(None, ge=1),
    title: str | None = None,
    content: str | None = None,
    limit: int = Query(50, ge=0, le=1000),
    offset: int = Query(0, ge=0),
    sort_by: str | None = Query(None, pattern="^(id|userId|title|created|modified)$"),
    sort_dir: str = Query("desc", pattern="^(asc|desc)$"),
):
    session = SessionLocal()
    try:
        repo = IssueRepository(session)
        service = IssueService(repo)
        return service.list_issues(
            userId=userId,
            title=title,
            content=content,
            limit=limit,
            offset=offset,
            sort_by=sort_by,
            sort_dir=sort_dir,
        )
    except Exception as e:
        logging.error(f"Error listing issues: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.put("", response_model=IssueRead)
def update_issue_body(data: IssueUpdate):
    session = SessionLocal()
    try:
        repo = IssueRepository(session)
        service = IssueService(repo)
        updated = service.update_issue(data)
        if not updated:
            raise HTTPException(status_code=404, detail="Issue not found")
        session.expunge_all()
        return updated
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating issue {data.id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.put("/{issue_id}", response_model=IssueRead)
def update_issue(issue_id: int, data: IssueCreate):
    session = SessionLocal()
    try:
        repo = IssueRepository(session)
        service = IssueService(repo)
        update_data = IssueUpdate(id=issue_id, userId=data.userId, title=data.title, content=data.content)
        updated = service.update_issue(update_data)
        if not updated:
            raise HTTPException(status_code=404, detail="Issue not found")
        session.expunge_all()
        return updated
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating issue {issue_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.post("/{issue_id}/markers/{marker_id}", status_code=201)
def add_marker_to_issue(issue_id: int, marker_id: int):
    session = SessionLocal()
    try:
        repo = IssueRepository(session)
        service = IssueService(repo)
        success = service.add_marker_to_issue(issue_id, marker_id)
        if not success:
            raise HTTPException(status_code=404, detail="Issue or Marker not found")
        return {"message": "Marker added to issue successfully"}
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error adding marker {marker_id} to issue {issue_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.delete("/{issue_id}", status_code=204)
def delete_issue(issue_id: int):
    session = SessionLocal()
    try:
        repo = IssueRepository(session)
        service = IssueService(repo)
        success = service.delete_issue(issue_id)
        if not success:
            raise HTTPException(status_code=404, detail="Issue not found")
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error deleting issue {issue_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

