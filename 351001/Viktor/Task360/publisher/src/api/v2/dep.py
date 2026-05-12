from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.ext.asyncio import AsyncSession
from Task360.publisher.src.infrastructure.database import get_db
from Task360.publisher.src.domain.repositories.sqlalchemy.creator_repository import SQLAlchemyCreatorRepository
from Task360.publisher.src.services.auth import decode_token

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/v2.0/login", auto_error=False)

async def get_current_user(token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db)):
    if not token:
        raise HTTPException(status_code=401, detail="Not authenticated")
    payload = decode_token(token)
    if not payload:
        raise HTTPException(status_code=401, detail="Invalid token")
    login = payload.get("sub")
    if not login:
        raise HTTPException(status_code=401, detail="Invalid token")
    repo = SQLAlchemyCreatorRepository(db)
    try:
        user = await repo.get_by_login(login)
    except KeyError:
        raise HTTPException(status_code=401, detail="User not found")
    return user

def require_role(required_role: str):
    def role_checker(current_user = Depends(get_current_user)):
        if current_user.role != required_role:
            raise HTTPException(status_code=403, detail="Insufficient permissions")
        return current_user
    return role_checker