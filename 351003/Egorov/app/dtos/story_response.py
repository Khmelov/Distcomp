from datetime import datetime
from typing import Any, Dict, List, Optional

from pydantic import BaseModel


class StoryResponseTo(BaseModel):
    id: int
    title: str
    content: str
    creator_id: int
    marker_ids: List[int]
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
    links: Optional[Dict[str, Any]] = None

