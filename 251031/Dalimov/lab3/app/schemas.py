from pydantic import BaseModel, Field

class UserBase(BaseModel):
    login: str = Field(..., min_length=4, max_length=64)
    password: str = Field(..., min_length=8)
    firstname: str = Field(..., min_length=2)
    lastname: str = Field(..., min_length=2)

class UserCreate(UserBase):
    pass

class UserUpdate(UserBase):
    id: int

class UserResponse(UserBase):
    id: int

    class Config:
        orm_mode = True
