from pydantic import BaseModel, Field, ConfigDict
from typing import Optional

class User(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    login: str
    password: str
    firstname: Optional[str] = ""
    lastname: Optional[str] = ""
