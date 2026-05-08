from pydantic import BaseModel, Field
from typing import Optional

class WriterRequestTo(BaseModel):
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128, pattern=r"^[A-Za-z0-9]+$")
    firstname: Optional[str] = Field(None, min_length=2, max_length=64)
    lastname: Optional[str] = Field(None, min_length=2, max_length=64)

class WriterResponseTo(BaseModel):
    id: int
    login: str
    firstname: Optional[str] = None
    lastname: Optional[str] = None

    class Config:
        from_attributes = True
