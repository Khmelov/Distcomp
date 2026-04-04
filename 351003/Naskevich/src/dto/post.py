from pydantic import BaseModel, Field


class PostRequestTo(BaseModel):
    tweet_id: int = Field(alias="tweetId")
    content: str = Field(min_length=2, max_length=2048)


class PostResponseTo(BaseModel):
    id: int
    tweet_id: int = Field(serialization_alias="tweetId")
    content: str

    model_config = {"from_attributes": True}
