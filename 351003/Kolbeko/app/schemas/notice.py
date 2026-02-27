from pydantic import BaseModel, Field
from typing import Optional

class NoticeRequestTo(BaseModel):
    id: Optional[int] = None
    tweetId: int
    content: str = Field(..., min_length=2, max_length=2048)

class NoticeResponseTo(BaseModel):
    id: int
    tweetId: int
    content: str