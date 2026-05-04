from fastapi import APIRouter

from src.api.dependencies import AuthServiceDep
from src.dto.auth import LoginRequest, LoginResponse

router = APIRouter(tags=["auth"])


@router.post("/login", response_model=LoginResponse)
async def login(data: LoginRequest, service: AuthServiceDep) -> LoginResponse:
    return await service.login(data)
