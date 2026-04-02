from typing import Any, Dict, Optional

from pydantic import BaseModel, EmailStr


class CreatorResponseTo(BaseModel):
    id: int
    login: str
    name: str
    email: EmailStr
    links: Optional[Dict[str, Any]] = None

