from pydantic import BaseModel, Field, ConfigDict
from datetime import datetime
from typing import List, Optional

# --- Comment DTOs ---
class CommentRequestTo(BaseModel):
    id: Optional[int] = None
    issueId: int
    content: str = Field(..., min_length=2, max_length=2048)
    country: Optional[str] = None

class CommentResponseTo(BaseModel):
    id: int
    issueId: int
    country: Optional[str] = None
    content: str
    model_config = ConfigDict(from_attributes=True)
