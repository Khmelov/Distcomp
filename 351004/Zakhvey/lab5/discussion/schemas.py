from pydantic import BaseModel, Field, ConfigDict
from typing import Optional

class CommentRequestTo(BaseModel):
    id: Optional[int] = None
    issueId: int
    content: str = Field(..., min_length=2, max_length=2048)
    country: Optional[str] = None
    model_config = ConfigDict(from_attributes=True)

class CommentResponseTo(BaseModel):
    id: int
    issueId: int
    country: Optional[str] = None
    content: str
    model_config = ConfigDict(from_attributes=True)
