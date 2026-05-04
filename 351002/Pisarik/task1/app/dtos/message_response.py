from typing import Any, Dict, Optional
from pydantic import BaseModel

class MessageResponseTo(BaseModel):
    id: int
    newsId: int
    content: str
    links: Optional[Dict[str, Any]] = None
