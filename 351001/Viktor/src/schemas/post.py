from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel

class PostBase(BaseModel):
    content: str = Field(..., min_length=4, max_length=2048)
    tweet_id: int

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, populate_by_name=True)

class PostRequestTo(PostBase):
    pass

class PostResponseTo(PostBase):
    id: int