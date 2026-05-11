from typing import Any, Dict, Optional

from pydantic import BaseModel, ConfigDict


class AuthorResponseTo(BaseModel):
    model_config = ConfigDict(ser_json_exclude_none=True)

    id: int
    login: str
    firstname: str
    lastname: str
    role: Optional[str] = None
    links: Optional[Dict[str, Any]] = None
