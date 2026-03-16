from pydantic import BaseModel, Field
from typing import Optional

class AuthorRequestTo(BaseModel):
    id: Optional[int] = None
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128)
    firstname: str = Field(..., min_length=2, max_length=64)
    lastname: str = Field(..., min_length=2, max_length=64)

class AuthorResponseTo(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str