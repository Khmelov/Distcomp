from fastapi import APIRouter, HTTPException, status
from sqlalchemy import select

from auth import verify_password, create_access_token
from dto import LoginRequestTo, LoginResponseTo
from models import Writer
from routers.db_router import db_dependency

router = APIRouter(
    prefix="/api/v2.0",
    tags=["auth"],
)


@router.post("/login", response_model=LoginResponseTo)
async def login(data: LoginRequestTo, db: db_dependency):
    result = await db.execute(select(Writer).where(Writer.login == data.login))
    user = result.scalars().first()

    if not user or not verify_password(data.password, user.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={"errorMessage": "Invalid login or password", "errorCode": 40101},
        )

    token = create_access_token(user.login, user.role or "CUSTOMER")
    return LoginResponseTo(access_token=token)
