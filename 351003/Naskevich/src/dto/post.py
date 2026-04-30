from pydantic import BaseModel, Field

from src.models.post_state import PostState


class PostRequestTo(BaseModel):
    tweet_id: int = Field(alias="tweetId")
    content: str = Field(min_length=2, max_length=2048)


class PostResponseTo(BaseModel):
    id: int
    tweet_id: int = Field(serialization_alias="tweetId")
    content: str
    state: PostState

    model_config = {"from_attributes": True}
