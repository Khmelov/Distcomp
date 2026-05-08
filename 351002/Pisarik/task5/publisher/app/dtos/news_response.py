from datetime import datetime
from typing import Any, Dict, Optional, List
from pydantic import BaseModel

class NewsResponseTo(BaseModel):
    id: int
    authorId: int
    title: str
    content: str
    created: datetime
    modified: datetime
    markIds: List[int]
    links: Optional[Dict[str, Any]] = None
