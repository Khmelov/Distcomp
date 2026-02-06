from pydantic import BaseModel, Field
from typing import List


class TopicRequestTo(BaseModel):
    title: str = Field(min_length=1)
    content: str = Field(min_length=1)
    userId: int
    markIds: List[int] = []


class TopicResponseTo(BaseModel):
    id: int
    title: str
    content: str
    userId: int
    markIds: List[int]
