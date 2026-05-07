from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel


class TagBase(BaseModel):
    name: str = Field(min_length=2, max_length=32)

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, validate_by_alias=True)

class TagRequestTo(TagBase):
    pass

class TagResponseTo(TagBase):
    id: int