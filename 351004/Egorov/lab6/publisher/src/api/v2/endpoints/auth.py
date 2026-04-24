from fastapi import APIRouter, Depends
from starlette import status

from src.deps import get_auth_service
from src.schemas import LoginRequest, LoginResponse
from src.services import AuthService

router = APIRouter()

@router.post("/login", response_model=LoginResponse, status_code=status.HTTP_200_OK)
async def login(request: LoginRequest, auth_service: AuthService = Depends(get_auth_service)):
    return await auth_service.login(request)