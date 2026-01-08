from pydantic import BaseModel, ConfigDict, Field


class Comment(BaseModel):
    # model_config = ConfigDict(from_attributes=True)
    issueId: int
    content: str = Field(min_length=2, max_length=2048)


class CommentID(Comment):
    id: int