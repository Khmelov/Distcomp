from fastapi import APIRouter, HTTPException, Query
from app.schemas.user import UserCreate, UserRead, UserUpdate
from app.services.user_service import UserService
from app.repositories.user_repository import UserRepository
from app.core.db import SessionLocal
import logging

router = APIRouter()

@router.post("", response_model=UserRead, status_code=201)
def create_user(user_create: UserCreate):
    session = SessionLocal()
    try:
        user_repo = UserRepository(session)
        user_service = UserService(user_repo)
        user = user_service.create_user(user_create)
        session.expunge_all()
        return user
    except ValueError as e:
        if "already exists" in str(e):
            raise HTTPException(status_code=403, detail="User with this login already exists")
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        logging.error(f"Error creating user: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.get("/{user_id}", response_model=UserRead)
def get_user(user_id: int):
    session = SessionLocal()
    try:
        user_repo = UserRepository(session)
        user_service = UserService(user_repo)
        user = user_service.get_user(user_id)
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
        return user
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error getting user {user_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.get("", response_model=list[UserRead])
def list_users(
    login: str | None = None,
    limit: int = Query(50, ge=0, le=1000),
    offset: int = Query(0, ge=0),
    sort_by: str | None = Query(None, pattern="^(id|login|firstname|lastname)$"),
    sort_dir: str = Query("desc", pattern="^(asc|desc)$"),
):
    session = SessionLocal()
    try:
        user_repo = UserRepository(session)
        user_service = UserService(user_repo)
        return user_service.list_users(
            login=login, limit=limit, offset=offset, sort_by=sort_by, sort_dir=sort_dir
        )
    except Exception as e:
        logging.error(f"Error listing users: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.delete("/{user_id}", status_code=204)
def delete_user(user_id: int):
    session = SessionLocal()
    try:
        user_repo = UserRepository(session)
        user_service = UserService(user_repo)
        success = user_service.delete_user(user_id)
        if not success:
            raise HTTPException(status_code=404, detail="User not found")
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error deleting user {user_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.put("", response_model=UserRead)
def update_user_body(user_update: UserUpdate):
    session = SessionLocal()
    try:
        user_repo = UserRepository(session)
        user_service = UserService(user_repo)
        user = user_service.update_user(user_update.id, user_update)
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
        session.expunge_all()
        return user
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating user {user_update.id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()

@router.put("/{user_id}", response_model=UserRead)
def update_user(user_id: int, user_update: UserCreate):
    session = SessionLocal()
    try:
        user_repo = UserRepository(session)
        user_service = UserService(user_repo)
        user = user_service.update_user(user_id, user_update)
        if not user:
            raise HTTPException(status_code=404, detail="User not found")
        session.expunge_all()
        return user
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating user {user_id}: {e}")
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")
    finally:
        session.close()
