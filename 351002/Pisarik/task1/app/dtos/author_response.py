from typing import Any, Dict, Optional
from pydantic import BaseModel

class AuthorResponseTo(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str
    links: Optional[Dict[str, Any]] = None
