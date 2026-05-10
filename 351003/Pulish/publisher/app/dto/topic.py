from pydantic import BaseModel, Field
from typing import List


class TopicRequestTo(BaseModel):
    id: int | None = None
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=2, max_length=2048)
    userId: int
    marks: List[str] = []


class TopicResponseTo(BaseModel):
    id: int
    title: str
    content: str
    userId: int
    markIds: List[int]
