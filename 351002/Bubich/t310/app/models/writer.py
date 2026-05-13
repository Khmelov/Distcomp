from dataclasses import dataclass
from app.models.base import BaseEntity

@dataclass
class Writer(BaseEntity):
    login: str = ""
    password: str = ""
    firstname: str = ""
    lastname: str = ""