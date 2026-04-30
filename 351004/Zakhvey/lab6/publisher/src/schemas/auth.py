from pydantic import BaseModel, Field

from src.domain.models.models import UserRole

class AuthBase(BaseModel):
    login: str = Field(min_length=2, max_length=64)

class LoginRequest(AuthBase):
    password: str = Field(min_length=8, max_length=128)

class GenerateTokenRequest(AuthBase):
    role: UserRole

class LoginResponse(BaseModel):
    access_token: str

class ValidTokenResponse(AuthBase):
    role: UserRole