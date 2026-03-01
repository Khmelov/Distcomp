from pydantic import BaseModel, Field


class UserRequestTo(BaseModel):
    login: str = Field(min_length=3)
    password: str = Field(min_length=6)
    firstname: str
    lastname: str


class UserResponseTo(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str
