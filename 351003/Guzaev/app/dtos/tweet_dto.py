from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

class TweetRequestTo(BaseModel):
    writer_id: int = Field(..., alias="writerId")
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)
    marker_ids: Optional[List[int]] = Field(default_factory=list, alias="markerIds")

    class Config:
        populate_by_name = True

class TweetResponseTo(BaseModel):
    id: int
    writer_id: int = Field(..., alias="writerId")
    title: str
    content: str
    created: datetime
    modified: datetime
    marker_ids: List[int] = Field(default_factory=list, alias="markerIds")

    class Config:
        from_attributes = True
        populate_by_name = True
