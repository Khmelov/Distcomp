from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class TagBase(BaseModel):
    name: str

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, validate_by_alias=True)

class TagRequestTo(TagBase):
    pass

class TagResponseTo(TagBase):
    id: int