from pydantic import BaseModel, Field

class UserCreate(BaseModel):
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(max_length=64)
    lastname: str = Field(max_length=64)

class UserRead(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str

class UserUpdate(BaseModel):
    id: int
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(max_length=64)
    lastname: str = Field(max_length=64)