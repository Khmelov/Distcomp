from fastapi import APIRouter, status, Depends
from sqlalchemy.orm import Session
from app.db.database import get_db
from app.dto.user import UserRequestTo, UserResponseTo
from app.services.user_service import UserService

router = APIRouter()


@router.post("/users",
             response_model=UserResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_user(dto: UserRequestTo, db: Session = Depends(get_db)):
    return UserService(db).create(dto)


@router.get("/users", response_model=list[UserResponseTo])
def get_users(db: Session = Depends(get_db)):
    return UserService(db).find_all()


@router.get("/users/{id}", response_model=UserResponseTo)
def get_user(id: int, db: Session = Depends(get_db)):
    return UserService(db).find_by_id(id)


@router.put("/users/{id}", response_model=UserResponseTo)
def update_user(id: int, dto: UserRequestTo, db: Session = Depends(get_db)):
    return UserService(db).update(id, dto)


@router.delete("/users/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(id: int, db: Session = Depends(get_db)):
    UserService(db).delete(id)
