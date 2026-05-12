from fastapi import APIRouter, status
from src.schemas.dto import LoginRequestTo, TokenResponseTo
from src.models.editor import Editor
from src.core.security import verify_password, create_access_token
from src.core.exceptions import BaseAppException


router = APIRouter()


@router.post("/login", response_model=TokenResponseTo, status_code=status.HTTP_200_OK)
async def login(credentials: LoginRequestTo):
    user = await Editor.get_or_none(login=credentials.login)
    if not user or not verify_password(credentials.password, user.password):
        raise BaseAppException(401, "40103", "Incorrect login or password")

    access_token = create_access_token(data={
        "sub": user.login,
        "role": user.role
    })

    return TokenResponseTo(access_token=access_token)