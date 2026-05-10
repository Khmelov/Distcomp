from datetime import datetime

from pydantic import BaseModel, Field


class IssueRequestTo(BaseModel):
    id: int | None = None
    userId: int = Field(gt=0)
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=4, max_length=2048)
    stickerIds: list[int] = Field(default_factory=list)


class IssueResponseTo(BaseModel):
    id: int
    userId: int
    title: str
    content: str
    created: datetime
    modified: datetime
    stickerIds: list[int]
