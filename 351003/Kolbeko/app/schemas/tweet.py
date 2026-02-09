from pydantic import BaseModel, Field
from typing import List, Optional

class TweetRequestTo(BaseModel):
    id: Optional[int] = None
    authorId: int
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)
    labelIds: List[int] = []

class TweetResponseTo(BaseModel):
    id: int
    authorId: int
    title: str
    content: str
    created: str
    modified: str