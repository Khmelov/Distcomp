from pydantic import BaseModel, Field


class CommentRequestTo(BaseModel):
    content: str = Field(min_length=2, max_length=2048)
    topicId: int


class CommentResponseTo(BaseModel):
    id: int
    content: str
    topicId: int
