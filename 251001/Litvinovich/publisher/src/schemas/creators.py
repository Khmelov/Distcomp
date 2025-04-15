from pydantic import BaseModel, Field

from src.models.models import Creator


class CreatorBase(BaseModel):
    login: str = Field(min_length=2, max_length=64)
    firstname: str = Field(min_length=2, max_length=64)
    lastname: str = Field(min_length=2, max_length=64)

    model_config = {
        'from_attributes': True,
    }

class CreatorRequestToAdd(CreatorBase):
    password: str = Field(min_length=8, max_length=128)

class CreatorRequestToUpdate(CreatorRequestToAdd):
    id: int

class CreatorResponseTo(CreatorBase):
    id: int
