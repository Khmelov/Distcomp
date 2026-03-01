from pydantic import BaseModel, Field
from typing import List, Optional

class IssueRequestTo(BaseModel):
    id: Optional[int] = None
    authorId: int
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)
    stickerIds: List[int] = []

class IssueResponseTo(BaseModel):
    id: int
    authorId: int
    title: str
    content: str
    created: str
    modified: str
