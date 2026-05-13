from pydantic import BaseModel, Field
from fastapi import APIRouter, HTTPException, status

from app.auth.jwt_handler import create_access_token
from app.auth.password import hash_password, verify_password
from app.models.creator import Creator, CreatorRole


router = APIRouter(prefix="/api/v2.0", tags=["auth"])


class CreatorRegistrationRequest(BaseModel):
    login: str = Field(min_length=1, max_length=50)
    password: str = Field(min_length=6, max_length=256)
    firstName: str = Field(min_length=1, max_length=100)
    lastName: str = Field(min_length=1, max_length=100)
    role: CreatorRole


class CreatorAuthResponse(BaseModel):
    id: int
    login: str
    firstName: str
    lastName: str
    role: CreatorRole


class LoginRequest(BaseModel):
    login: str = Field(min_length=1, max_length=50)
    password: str = Field(min_length=1, max_length=256)


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "Bearer"


def _find_creator_by_login(login: str) -> Creator | None:
    from main import creator_repository

    for creator in creator_repository.read_all():
        if creator.login == login:
            return creator
    return None


@router.post("/creators", response_model=CreatorAuthResponse, status_code=status.HTTP_201_CREATED)
def register_creator(payload: CreatorRegistrationRequest) -> CreatorAuthResponse:
    from main import creator_repository

    if _find_creator_by_login(payload.login):
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Login already exists")

    entity = Creator(
        login=payload.login,
        password=hash_password(payload.password),
        first_name=payload.firstName,
        last_name=payload.lastName,
        role=payload.role,
        name=f"{payload.firstName} {payload.lastName}".strip(),
        email=f"{payload.login}@example.com",
    )
    created = creator_repository.create(entity)

    return CreatorAuthResponse(
        id=created.id or 0,
        login=created.login,
        firstName=created.first_name,
        lastName=created.last_name,
        role=created.role,
    )


@router.post("/login", response_model=TokenResponse)
def login(payload: LoginRequest) -> TokenResponse:
    user = _find_creator_by_login(payload.login)
    try:
        res = verify_password(payload.password, user.password)
    except Exception:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid credentials")
    if not user or not res:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid credentials")

    access_token = create_access_token(user.login, user.role)
    return TokenResponse(access_token=access_token)
