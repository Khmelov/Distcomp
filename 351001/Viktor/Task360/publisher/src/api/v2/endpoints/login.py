from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from Task360.publisher.src.services.auth import verify_password, create_access_token
from Task360.publisher.src.domain.repositories.sqlalchemy.creator_repository import SQLAlchemyCreatorRepository
from Task360.publisher.src.infrastructure.database import get_db

router = APIRouter(prefix="/login")

class LoginRequest(BaseModel):
    login: str
    password: str

@router.post("")
async def login(data: LoginRequest, db=Depends(get_db)):
    repo = SQLAlchemyCreatorRepository(db)
    try:
        user = await repo.get_by_login(data.login)
    except KeyError:
        raise HTTPException(status_code=401, detail="Invalid credentials")
    if not verify_password(data.password, user.hashed_password):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    token = create_access_token({"sub": user.login, "role": user.role})
    return {"access_token": token, "token_type": "bearer"}