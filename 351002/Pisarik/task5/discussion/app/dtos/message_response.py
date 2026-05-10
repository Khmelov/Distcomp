from typing import Any, Dict, Optional

from pydantic import BaseModel


class MessageResponseTo(BaseModel):
    id: int
    newsId: int
    content: str
    state: Optional[str] = None
    country: Optional[str] = None
    links: Optional[Dict[str, Any]] = None
