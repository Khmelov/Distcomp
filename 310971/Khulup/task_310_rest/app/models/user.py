from pydantic import BaseModel
from typing import Optional

class User(BaseModel):
    id: int
    login: str
    password: str
    firstname: Optional[str] = ""
    lastname: Optional[str] = ""
