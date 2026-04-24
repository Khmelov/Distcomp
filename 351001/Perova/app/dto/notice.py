from pydantic import BaseModel, Field


class NoticeRequestTo(BaseModel):
    id: int | None = None
    issueId: int = Field(gt=0)
    content: str = Field(min_length=2, max_length=2048)


class NoticeResponseTo(BaseModel):
    id: int
    issueId: int
    content: str
