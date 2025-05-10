from sqlalchemy import Column, Integer, String
from database import Base

class User(Base):
    __tablename__ = "tbl_user"
    id = Column(Integer, primary_key=True, index=True)
    login = Column(String(64), unique=True, index=True, nullable=False)
    password = Column(String, nullable=False)
    firstname = Column(String, nullable=False)
    lastname = Column(String, nullable=False)
"""

schemas_py = """\
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