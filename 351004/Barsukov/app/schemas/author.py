from pydantic import BaseModel, Field
from typing import Optional

class AuthorRequestTo(BaseModel):
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128)
    firstname: str
    lastname: str
    role: Optional[str] = "CUSTOMER"

class AuthorResponseTo(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str
    role: str # Добавь роль в ответ

class LoginRequest(BaseModel):
    login: str
    password: str

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"