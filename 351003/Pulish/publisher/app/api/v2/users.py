from fastapi import APIRouter, status, Depends, HTTPException
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.dto.user import UserRequestTo, UserResponseTo
from app.services.user_service import UserService
from app.core.security import get_current_user, require_admin
from app.models.user import User

router = APIRouter()


@router.post("/users", response_model=UserResponseTo,
             status_code=status.HTTP_201_CREATED)
def register_user(dto: UserRequestTo, db: Session = Depends(get_db)):
    return UserService(db).create(dto)


@router.get("/users", response_model=list[UserResponseTo])
def get_users(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return UserService(db).find_all()


@router.get("/users/{id}", response_model=UserResponseTo)
def get_user(
    id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return UserService(db).find_by_id(id)


@router.put("/users", response_model=UserResponseTo)
def update_user(
    dto: UserRequestTo,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    if current_user.role != "ADMIN" and current_user.id != dto.id:
        raise HTTPException(
            status_code=403,
            detail={"errorMessage": "Forbidden", "errorCode": 40300},
        )
    return UserService(db).update(dto)


@router.delete("/users/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(
    id: int,
    db: Session = Depends(get_db),
    _: User = Depends(require_admin),
):
    UserService(db).delete(id)
