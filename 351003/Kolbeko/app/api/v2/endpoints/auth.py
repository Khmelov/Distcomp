from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.core.database import get_db
from app.core.security import verify_password, create_access_token
from app.core.exceptions import AppException
from app.models.author import Author
from app.schemas.author import LoginRequest, TokenResponse

router = APIRouter()

@router.post("/login", response_model=TokenResponse)
async def login(req: LoginRequest, session: AsyncSession = Depends(get_db)):
    result = await session.execute(select(Author).where(Author.login == req.login))
    user = result.scalar_one_or_none()
    
    if not user or not verify_password(req.password, user.password):
        raise AppException(401, "Invalid credentials", 40102)
        
    token = create_access_token(data={
        "sub": user.login, 
        "id": user.id,
        "role": user.role
    })
    return TokenResponse(access_token=token)