from pydantic import BaseModel, Field
from typing import Optional

class LabelRequestTo(BaseModel):
    id: Optional[int] = None
    name: str = Field(..., min_length=2, max_length=32)

class LabelResponseTo(BaseModel):
    id: int
    name: str