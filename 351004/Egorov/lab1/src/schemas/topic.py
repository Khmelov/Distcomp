from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class TopicBase(BaseModel):
    title: str
    content: str
    author_id: int

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, validate_by_name=True)

class TopicRequestTo(TopicBase):
    pass

class TopicResponseTo(TopicBase):
    id: int