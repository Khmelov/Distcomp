from pydantic import BaseModel, Field
from typing import Optional

class StickerRequestTo(BaseModel):
    id: Optional[int] = None
    name: str = Field(..., min_length=2, max_length=32)

class StickerResponseTo(BaseModel):
    id: int
    name: str