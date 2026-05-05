from typing import List

from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel


class TopicBase(BaseModel):
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=2, max_length=2048)
    author_id: int

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, validate_by_name=True)

class TopicRequestTo(TopicBase):
    tags: List[str] | None = []
    pass

class TopicResponseTo(TopicBase):
    id: int