from pydantic import BaseModel, ConfigDict


class Comment(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    newsId: int
    content: str


class CommentID(Comment):
    id: int