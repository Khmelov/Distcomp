from datetime import datetime
from typing import List, Optional

from pydantic import BaseModel, Field


class StoryBase(BaseModel):
    title: str = Field(..., min_length=1, max_length=255)
    content: str = Field(..., min_length=1)
    creator_id: int
    marker_ids: List[int] = Field(default_factory=list)


class StoryCreate(StoryBase):
    pass


class StoryUpdate(StoryBase):
    pass


class StoryRead(BaseModel):
    id: int
    title: str
    content: str
    creator_id: int
    marker_ids: List[int]
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True

