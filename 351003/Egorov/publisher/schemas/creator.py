from pydantic import BaseModel, EmailStr, Field

from publisher.schemas.common import Timestamped


class CreatorBase(BaseModel):
    login: str = Field(..., min_length=1, max_length=255)
    name: str = Field(..., min_length=1, max_length=255)
    email: EmailStr


class CreatorCreate(CreatorBase):
    pass


class CreatorUpdate(CreatorBase):
    pass


class CreatorRead(Timestamped):
    id: int
    login: str
    name: str
    email: EmailStr

    class Config:
        from_attributes = True

