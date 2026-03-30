from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel

class CreatorBase(BaseModel):
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128)
    firstname: str = Field(..., min_length=2, max_length=64)
    lastname: str = Field(..., min_length=2, max_length=64)

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, populate_by_name=True)

class CreatorRequestTo(CreatorBase):
    pass

class CreatorResponseTo(CreatorBase):
    id: int