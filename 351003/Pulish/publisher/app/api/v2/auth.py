from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.models.user import User
from app.core.security import verify_password, create_access_token

router = APIRouter()


class LoginRequest(BaseModel):
    login: str
    password: str


class LoginResponse(BaseModel):
    access_token: str
    token_type: str = "Bearer"


@router.post("/login", response_model=LoginResponse)
def login(dto: LoginRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.login == dto.login).first()
    if not user or not verify_password(dto.password, user.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={"errorMessage": "Invalid credentials", "errorCode": 40100},
        )
    token = create_access_token(login=user.login, role=user.role)
    return LoginResponse(access_token=token)
