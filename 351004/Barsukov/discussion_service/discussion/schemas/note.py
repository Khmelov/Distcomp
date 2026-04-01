from pydantic import BaseModel, Field
from typing import Optional, Literal

class NoteRequestTo(BaseModel):
    id: Optional[int] = None
    issueId: int
    content: str = Field(..., min_length=2, max_length=2048)
    state: Optional[Literal["PENDING", "APPROVE", "DECLINE"]] = "PENDING"

class NoteResponseTo(BaseModel):
    id: int
    issueId: int
    content: str
    state: str