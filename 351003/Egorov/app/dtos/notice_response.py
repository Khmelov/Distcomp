from typing import Any, Dict, Optional

from pydantic import BaseModel


class NoticeResponseTo(BaseModel):
    id: int
    content: str
    story_id: int
    links: Optional[Dict[Any, Any]] = None

