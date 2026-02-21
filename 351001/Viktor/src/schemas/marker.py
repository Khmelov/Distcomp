from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel

class MarkerBase(BaseModel):
    name: str = Field(..., min_length=2, max_length=32)

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, populate_by_name=True)

class MarkerRequestTo(MarkerBase):
    pass

class MarkerResponseTo(MarkerBase):
    id: int