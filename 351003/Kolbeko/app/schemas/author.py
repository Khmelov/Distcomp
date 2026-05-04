from pydantic import BaseModel, Field, ConfigDict
from typing import Optional

class AuthorRequestTo(BaseModel):
    id: Optional[int] = None
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128)
    firstname: str = Field(..., min_length=2, max_length=64)
    lastname: str = Field(..., min_length=2, max_length=64)
    role: Optional[str] = "CUSTOMER"

class AuthorResponseTo(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)

    id: int
    login: str
    firstname: str
    lastname: str
    role: str = "CUSTOMER"

class LoginRequest(BaseModel):
    login: str
    password: str

class TokenResponse(BaseModel):
    access_token: str
    type_token: str = "Bearer"