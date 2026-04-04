from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel

class TweetBase(BaseModel):
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)
    creator_id: int

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, populate_by_name=True)

class TweetRequestTo(TweetBase):
    pass

class TweetResponseTo(TweetBase):
    id: int