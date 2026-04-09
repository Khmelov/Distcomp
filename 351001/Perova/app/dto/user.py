from pydantic import BaseModel, Field


class UserRequestTo(BaseModel):
    id: int | None = None
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(min_length=2, max_length=64)
    lastname: str = Field(min_length=2, max_length=64)


class UserResponseTo(BaseModel):
    id: int
    login: str
    password: str
    firstname: str
    lastname: str
