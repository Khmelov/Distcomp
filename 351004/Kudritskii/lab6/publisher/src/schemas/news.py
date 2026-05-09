from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel


class NewsBase(BaseModel):
    content: str = Field(min_length=2, max_length=2048)
    notice_id: int

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, validate_by_name=True)

class NewsRequestTo(NewsBase):
    pass

class NewsResponseTo(NewsBase):
    id: int