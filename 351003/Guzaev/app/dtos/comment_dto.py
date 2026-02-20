from pydantic import BaseModel, Field

class CommentRequestTo(BaseModel):
    tweet_id: int = Field(..., alias="tweetId")
    content: str = Field(..., min_length=2, max_length=2048)

    class Config:
        populate_by_name = True

class CommentResponseTo(BaseModel):
    id: int
    tweet_id: int = Field(..., alias="tweetId")
    content: str

    class Config:
        from_attributes = True
        populate_by_name = True
