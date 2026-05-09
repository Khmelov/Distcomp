from typing import Any, Dict, Optional

from pydantic import BaseModel


class MarkerResponseTo(BaseModel):
    id: int
    name: str
    links: Optional[Dict[str, Any]] = None

